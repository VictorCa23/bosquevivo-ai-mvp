# Start Postgres+PGVector via Docker Compose and wait for healthcheck.
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

docker compose up -d
$cid = docker compose ps -q postgres 2>$null
if (-not $cid) {
    Write-Error "Postgres container not found. Check docker compose up."
    exit 1
}

$max = 60
$n = 0
do {
    $status = docker inspect --format '{{.State.Health.Status}}' $cid 2>$null
    if ($status -eq "healthy") {
        Write-Host "Postgres is healthy."
        exit 0
    }
    $n++
    if ($n -ge $max) {
        Write-Error "Healthcheck did not pass within timeout."
        exit 1
    }
    Start-Sleep -Seconds 2
} while ($true)
