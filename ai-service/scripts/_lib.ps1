Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Ok($m){   Write-Host "[OK]  $m" -ForegroundColor Green }
function Write-Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Write-Err($m){  Write-Host "[ERR] $m" -ForegroundColor Red }

function Resolve-RepoRoot {
  # scripts\*.ps1 => repo root = ..\ (one level above scripts)
  return (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
}

function Ensure-Dirs([string[]]$paths) {
  foreach($p in $paths){
    if(-not (Test-Path $p)){
      New-Item -ItemType Directory -Force -Path $p | Out-Null
    }
  }
}

function Get-ListeningOnPort([int]$port) {
  try {
    $conns = @(Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction Stop)
    return $conns
  } catch {
    return @()
  }
}

function Assert-Port-Free([int]$port, [string]$label="port") {
  $c = @(Get-ListeningOnPort $port)
  if($c.Count -gt 0){
    $pids = ($c | Select-Object -ExpandProperty OwningProcess -Unique) -join ","
    throw "$label $port is already in use (PID(s): $pids)"
  }
}

function Curl-Http([string]$url, [int]$timeoutSec=3) {
  # IMPORTANT: use curl.exe, NOT "curl" alias (PowerShell aliases curl -> Invoke-WebRequest)
  $out = & curl.exe --noproxy "*" -sS --max-time $timeoutSec $url 2>$null
  return $out
}

function Curl-StatusCode([string]$url, [int]$timeoutSec=3) {
  $code = & curl.exe --noproxy "*" -sS --max-time $timeoutSec -o NUL -w "%{http_code}" $url 2>$null
  if([string]::IsNullOrWhiteSpace($code)) { return -1 }
  return [int]$code
}

function Wait-HealthUp([string]$baseUrl, [int]$timeoutSec=180, [int]$intervalSec=2) {
  $base = $baseUrl.TrimEnd("/")
  $healthUrl = "$base/actuator/health"

  Write-Info "Waiting for health: $healthUrl (timeout ${timeoutSec}s)"
  $deadline = (Get-Date).AddSeconds($timeoutSec)

  while((Get-Date) -lt $deadline){
    try {
      $code = Curl-StatusCode $healthUrl 3
      if($code -eq 200){
        $body = Curl-Http $healthUrl 3
        if(-not [string]::IsNullOrWhiteSpace($body)){
          try {
            $json = $body | ConvertFrom-Json
            if($json.status -eq "UP"){
              return @{ ok=$true; url=$healthUrl; code=200; status=$json.status }
            }
          } catch {
            # Si no parsea JSON, igual consideramos que 200 es “arriba”
            return @{ ok=$true; url=$healthUrl; code=200; status="(unparsed)" }
          }
        }
      }
    } catch {
      # ignore and retry
    }
    Start-Sleep -Seconds $intervalSec
  }

  return @{ ok=$false; url=$healthUrl; code=-1; status="TIMEOUT" }
}

function Tail-File([string]$path, [int]$lines=120) {
  if(Test-Path $path){
    return (Get-Content -Path $path -Tail $lines -ErrorAction SilentlyContinue) -join "`n"
  }
  return ""
}

function Stop-ByPid([int]$procId, [int]$graceSec=5) {
  if($procId -le 0){ return }
  $p = Get-Process -Id $procId -ErrorAction SilentlyContinue
  if(-not $p){ return }

  Write-Warn "Stopping process PID=$procId ..."
  try {
    Stop-Process -Id $procId -ErrorAction SilentlyContinue
    Start-Sleep -Seconds $graceSec
  } catch {}

  $p2 = Get-Process -Id $procId -ErrorAction SilentlyContinue
  if($p2){
    Write-Warn "Force-killing process PID=$procId ..."
    Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
  }
}
