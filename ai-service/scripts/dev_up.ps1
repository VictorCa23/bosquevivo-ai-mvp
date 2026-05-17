param(
  [string]$Profile = "dev",
  [int]$Port = 8091,
  [string]$BaseUrl = "",
  [int]$HealthTimeoutSec = 180,
  [switch]$SkipBuild,
  [switch]$KeepRunningOnFailure
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

. "$PSScriptRoot\_lib.ps1"

$repoRoot = Resolve-RepoRoot
if([string]::IsNullOrWhiteSpace($BaseUrl)){ $BaseUrl = "http://localhost:$Port" }
$BaseUrl = $BaseUrl.TrimEnd("/")

Write-Info "Dev up: profile=$Profile port=$Port base=$BaseUrl"
Write-Info "RepoRoot=$repoRoot"

$runtime = Join-Path $repoRoot ".runtime"
$pidDir  = Join-Path $runtime "pids"
$logDir  = Join-Path $runtime "logs"
$tmpDir  = Join-Path $runtime "tmp"
Ensure-Dirs @($runtime, $pidDir, $logDir, $tmpDir)

# 1) Port must be free
Assert-Port-Free -port $Port -label "ai-service port"
Write-Ok "ai-service: port $Port is free"

# 2) Build (optional)
if(-not $SkipBuild){
  Write-Info "Building (ai-bootstrap + deps) with Maven (skipTests)..."
  Push-Location $repoRoot
  try {
    & "$repoRoot\mvnw.cmd" -DskipTests -pl "modules/ai-bootstrap" -am clean package
  } finally {
    Pop-Location
  }
  Write-Ok "Build OK"
} else {
  Write-Warn "SkipBuild enabled."
}

# 3) Find jar
$jar = Get-ChildItem -Path (Join-Path $repoRoot "modules\ai-bootstrap\target") -Filter "ai-bootstrap-*.jar" `
  | Where-Object { $_.Name -notlike "*.original" } `
  | Sort-Object LastWriteTime -Descending `
  | Select-Object -First 1

if(-not $jar){
  throw "Jar not found under modules\ai-bootstrap\target (run build first)."
}
Write-Info "Jar: $($jar.FullName)"

# 4) Start background process
$pidFile = Join-Path $pidDir "ai-bootstrap.pid"
$outLog  = Join-Path $logDir "ai-bootstrap.out.log"
$errLog  = Join-Path $logDir "ai-bootstrap.err.log"

# Limpia logs anteriores (enterprise: append también es válido; acá lo dejamos limpio para debugging)
"" | Out-File -FilePath $outLog -Encoding UTF8
"" | Out-File -FilePath $errLog -Encoding UTF8

$javaArgs = @(
  "-jar", $jar.FullName,
  "--spring.profiles.active=$Profile",
  "--server.port=$Port"
)

Write-Info "Starting ai-bootstrap (background)..."
$proc = Start-Process -FilePath "java" -ArgumentList $javaArgs -WorkingDirectory $repoRoot `
  -RedirectStandardOutput $outLog -RedirectStandardError $errLog -PassThru -WindowStyle Hidden

$procId = $proc.Id
Write-Ok "Started java PID=$procId"
Set-Content -Path $pidFile -Value $procId -Encoding ASCII
Write-Ok "PID saved: $pidFile"
Write-Info "Logs: $outLog / $errLog"

# 5) Wait health
$health = Wait-HealthUp -baseUrl $BaseUrl -timeoutSec $HealthTimeoutSec -intervalSec 2
if($health.ok){
  Write-Ok "Health OK: $($health.url) => $($health.status)"
  exit 0
}

Write-Err "Health not ready within timeout."
Write-Err "Tail stderr:`n$(Tail-File $errLog 160)"
Write-Err "Tail stdout:`n$(Tail-File $outLog 160)"

if($KeepRunningOnFailure){
  Write-Warn "KeepRunningOnFailure enabled: leaving process running (PID=$procId)."
  throw "dev_up failed (health not UP) but process kept running."
} else {
  Stop-ByPid -procId $procId
  throw "dev_up failed (health not UP)."
}
