# Build and run ai-service in dev profile
# Usage: .\scripts\dev.ps1
# Sets AI_SERVICE_PORT and IAM_ISSUER_URI environment variables

Write-Host "Building ai-service (skipping tests)..." -ForegroundColor Cyan
.\mvnw.cmd -DskipTests clean install
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Build successful!" -ForegroundColor Green
Write-Host "Starting ai-service in dev profile..." -ForegroundColor Cyan

# Set environment variables
$env:AI_SERVICE_PORT = if ($env:AI_SERVICE_PORT) { $env:AI_SERVICE_PORT } else { "8091" }
$env:IAM_ISSUER_URI = if ($env:IAM_ISSUER_URI) { $env:IAM_ISSUER_URI } else { "http://localhost:8080/realms/solveria" }

Write-Host "AI_SERVICE_PORT: $env:AI_SERVICE_PORT" -ForegroundColor Yellow
Write-Host "IAM_ISSUER_URI: $env:IAM_ISSUER_URI" -ForegroundColor Yellow

# Run the application
.\mvnw.cmd -pl modules/ai-bootstrap spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
