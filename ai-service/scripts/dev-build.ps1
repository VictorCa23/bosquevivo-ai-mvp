# Build script for ai-service
# Compiles all modules without running tests
Write-Host "Building ai-service (skipping tests)..." -ForegroundColor Cyan
.\mvnw.cmd -DskipTests clean install
if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful!" -ForegroundColor Green
} else {
    Write-Host "Build failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}
