param(
  [int]$Port = 0
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

. "$PSScriptRoot\_lib.ps1"

$repoRoot = Resolve-RepoRoot
$pidFile  = Join-Path $repoRoot ".runtime\pids\ai-bootstrap.pid"

if(Test-Path $pidFile){
  $raw = (Get-Content $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1)
  if($raw -match '^\d+$'){
    $procId = [int]$raw
    Stop-ByPid -procId $procId
  } else {
    Write-Warn "PID file exists but invalid content: $pidFile"
  }
  Remove-Item $pidFile -Force -ErrorAction SilentlyContinue
  Write-Ok "Removed pid file: $pidFile"
} else {
  Write-Warn "No pid file found: $pidFile (nothing to stop)"
}

if($Port -gt 0){
  $c = @(Get-ListeningOnPort $Port)
  if($c.Count -gt 0){
    $pids = $c | Select-Object -ExpandProperty OwningProcess -Unique
    foreach($p in $pids){
      Stop-ByPid -procId $p
    }
    Write-Ok "Stopped processes listening on port $Port"
  } else {
    Write-Info "Port $Port not in use"
  }
}
