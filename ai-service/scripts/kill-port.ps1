# Kill process using a specific port
# Usage: .\scripts\kill-port.ps1 [port]
# Default port: 8091

param(
    [int]$Port = 8091
)

Write-Host "Checking for process using port $Port..." -ForegroundColor Cyan

# Find process using the port
$process = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | 
    Select-Object -ExpandProperty OwningProcess -Unique

if ($process) {
    $processInfo = Get-Process -Id $process -ErrorAction SilentlyContinue
    if ($processInfo) {
        Write-Host "Found process: $($processInfo.ProcessName) (PID: $process)" -ForegroundColor Yellow
        Write-Host "Killing process..." -ForegroundColor Yellow
        Stop-Process -Id $process -Force
        Write-Host "Process killed successfully!" -ForegroundColor Green
    } else {
        Write-Host "Process ID $process not found or already terminated." -ForegroundColor Yellow
    }
} else {
    Write-Host "No process found using port $Port." -ForegroundColor Green
}
