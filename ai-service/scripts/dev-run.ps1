# Run script for ai-service in dev profile
# Usage: .\scripts\dev-run.ps1 [-Port 8082]
param(
    [int]$Port = 0
)

Write-Host "Starting ai-service in dev profile..." -ForegroundColor Cyan

$mvnArgs = @(
    "-pl", "modules/ai-bootstrap",
    "-Dspring-boot.run.profiles=dev"
)

if ($Port -gt 0) {
    Write-Host "Using custom port: $Port" -ForegroundColor Yellow
    $mvnArgs += "-Dspring-boot.run.arguments=--server.port=$Port"
}

$mvnArgs += "spring-boot:run"

.\mvnw.cmd $mvnArgs
