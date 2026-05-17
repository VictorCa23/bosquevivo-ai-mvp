[CmdletBinding()]
param(
  [string]$BaseUrl = $( if ($env:AI_BASE_URL) { $env:AI_BASE_URL } else { "http://localhost:8091" } ),
  [int]$TimeoutSec = 5,
  [int]$Retries = 1
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

# Evita "DiagnÃ³stico" en algunas consolas
try {
  [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
  $OutputEncoding = [System.Text.UTF8Encoding]::new()
} catch { }

function Write-Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Ok($m){   Write-Host "[OK]  $m" -ForegroundColor Green }
function Write-Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Write-Err($m){  Write-Host "[ERR] $m" -ForegroundColor Red }

function Ensure-SystemNetHttp {
  # Si el tipo no existe, intentamos cargar el assembly
  if (-not ("System.Net.Http.HttpClient" -as [type])) {
    try { Add-Type -AssemblyName "System.Net.Http" -ErrorAction Stop } catch { }
  }
}

function Get-CandidateBaseUrls([string]$base) {
  $u = [Uri]$base
  $list = New-Object System.Collections.Generic.List[string]
  $list.Add($base)

  # Si te pasan localhost, probamos 127.0.0.1 y ::1
  if ($u.Host -eq "localhost") {
    $list.Add(($base -replace "localhost", "127.0.0.1"))
    $list.Add(($base -replace "localhost", "[::1]"))
  }

  # Si te pasan 127.0.0.1, probamos localhost también (a veces DNS/stack)
  if ($u.Host -eq "127.0.0.1") {
    $list.Add(($base -replace "127.0.0.1", "localhost"))
  }

  # unique
  $seen = @{}
  $out = @()
  foreach ($x in $list) {
    if (-not $seen.ContainsKey($x)) { $seen[$x] = $true; $out += $x }
  }
  return $out
}

function Test-PortOpen([string]$TargetHost, [int]$Port, [int]$Ms = 800) {
  try {
    $client = New-Object System.Net.Sockets.TcpClient
    $iar = $client.BeginConnect($TargetHost, $Port, $null, $null)
    $ok = $iar.AsyncWaitHandle.WaitOne($Ms, $false)
    if (-not $ok) { $client.Close(); return $false }
    $client.EndConnect($iar)
    $client.Close()
    return $true
  } catch {
    return $false
  }
}

function Http-Get([string]$Url, [int]$T){
  Ensure-SystemNetHttp

  # Preferimos HttpClient si existe (más controlable)
  if ("System.Net.Http.HttpClient" -as [type]) {
    $handler = New-Object System.Net.Http.HttpClientHandler
    $handler.UseProxy = $false
    $client = New-Object System.Net.Http.HttpClient($handler)
    $client.Timeout = [TimeSpan]::FromSeconds($T)
    try {
      $resp = $client.GetAsync($Url).GetAwaiter().GetResult()
      $code = [int]$resp.StatusCode
      $body = $resp.Content.ReadAsStringAsync().GetAwaiter().GetResult()
      return @{ Code=$code; Body=$body }
    } finally {
      $client.Dispose()
      $handler.Dispose()
    }
  }

  # Fallback (si por alguna razón System.Net.Http no está disponible)
  try {
    $r = Invoke-WebRequest -Uri $Url -TimeoutSec $T -UseBasicParsing
    return @{ Code=[int]$r.StatusCode; Body=$r.Content }
  } catch {
    throw
  }
}

Write-Info "=== Smoke: Health ==="

$candidates = Get-CandidateBaseUrls $BaseUrl
$lastErr = $null

foreach ($b in $candidates) {
  $bu = [Uri]$b
  $targetHost = $bu.Host.Trim('[',']')   # NO usar $host (pisaría $Host)
  $port = if ($bu.Port -gt 0) { $bu.Port } else { 80 }
  $health = "$b/actuator/health"

  Write-Info "Trying: $health"

  if (-not (Test-PortOpen -TargetHost $targetHost -Port $port -Ms 800)) {
    Write-Warn "Port not reachable: $($targetHost):$port (TCP connect failed)."
    continue
  }

  for ($i=1; $i -le [Math]::Max(1,$Retries); $i++) {
    try {
      $r = Http-Get -Url $health -T $TimeoutSec

      if ($r.Code -ne 200) {
        Write-Warn "Health HTTP != 200 => $($r.Code)"
        $lastErr = "HTTP $($r.Code)"
        Start-Sleep -Milliseconds 250
        continue
      }

      $j = $null
      try { $j = $r.Body | ConvertFrom-Json } catch { }

      if (-not $j -or $j.status -ne "UP") {
        Write-Warn "Health JSON status != UP"
        $lastErr = "JSON status != UP"
        Start-Sleep -Milliseconds 250
        continue
      }

      Write-Ok "Health OK (UP) => $health"
      exit 0
    }
    catch {
      $lastErr = $_.Exception.Message
      Write-Warn "Request failed: $lastErr"
      if ($_.Exception.InnerException) {
        Write-Warn "Inner: $($_.Exception.InnerException.Message)"
      }
      Start-Sleep -Milliseconds 250
    }
  }
}

Write-Err "Health failed on all candidates. LastError=$lastErr"
Write-Info "Diagnóstico rápido:"
Write-Host "  1) Ver si el puerto escucha:  netstat -ano | findstr :8091"
Write-Host "  2) Probar directo:            curl http://127.0.0.1:8091/actuator/health"
Write-Host "  3) Revisar logs:              .runtime\logs\ai-bootstrap.err.log"
throw "Health not OK."
