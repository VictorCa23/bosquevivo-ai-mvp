<# 
  dev_db_down.ps1
  - Baja postgres via docker compose
  - Opcional: borra volumen (-WithVolumes) para reiniciar init scripts
#>

[CmdletBinding()]
param(
  [string]$ProjectName = "ai-service",
  [string]$ServiceName = "postgres",
  [switch]$WithVolumes
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Ok($m){ Write-Host "[OK]  $m" -ForegroundColor Green }
function Write-Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$composeFile = Join-Path $repoRoot "docker-compose.yml"
if (-not (Test-Path $composeFile)) {
  throw "No encontré docker-compose.yml en: $composeFile"
}

Write-Info "RepoRoot = $repoRoot"
Write-Info "Compose  = $composeFile"
Write-Info "Project  = $ProjectName"

if ($WithVolumes) {
  Write-Warn "Bajando compose + eliminando volúmenes (esto borra datos)."
  docker compose -p $ProjectName -f $composeFile down -v | Out-Null
  Write-Ok "Down -v OK."
} else {
  Write-Info "Bajando compose (sin borrar volúmenes)."
  docker compose -p $ProjectName -f $composeFile down | Out-Null
  Write-Ok "Down OK."
}

# Info: containers que podrían quedar colgados por publish
$hostPort = if ($env:AI_PG_HOST_PORT) { [int]$env:AI_PG_HOST_PORT } else { 5434 }
Write-Info "Si querés verificar puertos:"
Write-Host "docker ps --filter ""publish=$hostPort"" --format ""table {{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"""
