<# 
  dev_db_up.ps1
  - Levanta postgres (pgvector) con docker compose
  - Espera health=healthy
  - Crea/verifica extension vector
  - Imprime DB_URL / JDBC listo para usar
#>

[CmdletBinding()]
param(
  [string]$ProjectName = "ai-service",
  [string]$ServiceName = "postgres",
  [int]$HealthTimeoutSec = 180,
  [switch]$SkipVectorExtension,
  [switch]$ShowLogsOnFail
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Ok($m){ Write-Host "[OK]  $m" -ForegroundColor Green }
function Write-Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Write-Err($m){ Write-Host "[ERR] $m" -ForegroundColor Red }

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

# Detect compose file
$composeFile = Join-Path $repoRoot "docker-compose.yml"
if (-not (Test-Path $composeFile)) {
  throw "No encontré docker-compose.yml en: $composeFile"
}

# Env defaults aligned with docker-compose.yml and application-dev.yml
$bindHost = if ($env:AI_DB_BIND_HOST) { $env:AI_DB_BIND_HOST } else { "127.0.0.1" }
$hostPort = if ($env:AI_DB_PORT) { [int]$env:AI_DB_PORT } else { 5434 }
$dbName   = if ($env:AI_DB_NAME) { $env:AI_DB_NAME } else { "ai_service" }
$dbUser   = if ($env:AI_DB_USER) { $env:AI_DB_USER } else { "postgres" }

Write-Info "RepoRoot = $repoRoot"
Write-Info "Compose  = $composeFile"
Write-Info ("Target   = {0}:{1} -> postgres(5432) db={2} user={3}" -f $bindHost, $hostPort, $dbName, $dbUser)

# Basic docker sanity
try { docker version | Out-Null } catch { throw "Docker no responde. ¿Está corriendo Docker Desktop?" }
try { docker compose version | Out-Null } catch { throw "No encuentro 'docker compose'. Actualizá Docker Desktop." }

# Port conflict check (Windows)
$existing = @(Get-NetTCPConnection -LocalPort $hostPort -State Listen -ErrorAction SilentlyContinue)
if ($existing.Count -gt 0) {
  $publishUsers = docker ps --filter "publish=$hostPort" --format "{{.Names}}"
  if ($publishUsers -and ($publishUsers -notmatch "^$ProjectName")) {
    Write-Err "El puerto $hostPort ya está ocupado por:"
    docker ps --filter "publish=$hostPort" --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"
    throw "Liberá el puerto $hostPort o cambiá AI_PG_HOST_PORT (por ejemplo 5435)."
  } else {
    Write-Warn "Puerto $hostPort ya está en uso (probablemente por tu postgres actual). Continuo."
  }
}

# Up
Write-Info "docker compose up -d $ServiceName (project=$ProjectName)"
docker compose -p $ProjectName -f $composeFile up -d $ServiceName | Out-Null
Write-Ok "Compose up solicitado."

# Get container id reliably from compose
$containerId = (docker compose -p $ProjectName -f $composeFile ps -q $ServiceName).Trim()
if ([string]::IsNullOrWhiteSpace($containerId)) {
  $containerId = (docker ps -q --filter "label=com.docker.compose.project=$ProjectName" --filter "label=com.docker.compose.service=$ServiceName").Trim()
}
if ([string]::IsNullOrWhiteSpace($containerId)) {
  throw "No pude resolver el containerId del servicio '$ServiceName' (project=$ProjectName)."
}
Write-Info "ContainerId=$containerId"

# Wait for health
$deadline = (Get-Date).AddSeconds($HealthTimeoutSec)
$healthy = $false

while ((Get-Date) -lt $deadline) {
  $health = ""
  try { $health = (docker inspect -f "{{.State.Health.Status}}" $containerId).Trim() } catch { $health = "" }

  if ($health -eq "healthy") {
    $healthy = $true
    break
  }

  # Si no hay healthcheck (vacío), hacemos pg_isready
  if ([string]::IsNullOrWhiteSpace($health)) {
    try {
      docker exec $containerId pg_isready -U $dbUser -d $dbName -h localhost -p 5432 | Out-Null
      $healthy = $true
      break
    } catch { }
  }

  Start-Sleep -Seconds 2
}

if (-not $healthy) {
  Write-Err "Postgres no quedó healthy dentro de ${HealthTimeoutSec}s."
  if ($ShowLogsOnFail) {
    Write-Err "---- logs postgres (tail) ----"
    docker logs --tail 200 $containerId
  }
  throw "dev_db_up failed (postgres not healthy)."
}

Write-Ok "Postgres está healthy."

# Ensure extension vector
if (-not $SkipVectorExtension) {
  Write-Info "Asegurando extensión pgvector (CREATE EXTENSION IF NOT EXISTS vector)..."
  docker exec -i $containerId psql -U $dbUser -d $dbName -v ON_ERROR_STOP=1 -c "CREATE EXTENSION IF NOT EXISTS vector;" | Out-Null

  $ext = (docker exec -i $containerId psql -U $dbUser -d $dbName -tAc "SELECT extname||':'||extversion FROM pg_extension WHERE extname='vector';").Trim()
  if ($ext -match "vector:") {
    Write-Ok "pgvector OK => $ext"
  } else {
    throw "No se pudo verificar pgvector (no aparece en pg_extension)."
  }
} else {
  Write-Warn "SkipVectorExtension activado (no se creó/verificó pgvector)."
}

# Print ready-to-use connection strings (IMPORTANTE: usar ${} por el :)
$jdbcUrl = "jdbc:postgresql://localhost:${hostPort}/${dbName}"
$dbUrl   = "postgresql://${dbUser}:***@localhost:${hostPort}/${dbName}"

Write-Ok "DB listo."
Write-Host ""
Write-Host "==================== CONNECTION ====================" -ForegroundColor Magenta
Write-Host "JDBC_URL : $jdbcUrl"
Write-Host "DB_URL   : $dbUrl"
Write-Host "PSQL     : docker exec -it $containerId psql -U $dbUser -d $dbName"
Write-Host "====================================================" -ForegroundColor Magenta
Write-Host ""
