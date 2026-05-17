#!/usr/bin/env pwsh

[CmdletBinding()]
param(
    [switch]$SkipTests = $false
)

$ErrorActionPreference = "Stop"
$rootDir = Split-Path -Parent $PSScriptRoot
$testFlag = if ($SkipTests) { "-DskipTests" } else { "" }

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " BosqueVivo MVP Build" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Push-Location "$rootDir\bosquevivo-service"
try {
    mvn clean package $testFlag
    if ($LASTEXITCODE -ne 0) {
        throw "bosquevivo-service build failed with exit code $LASTEXITCODE"
    }
    Write-Host "[OK] bosquevivo-service built successfully" -ForegroundColor Green
} finally {
    Pop-Location
}

Push-Location "$rootDir\bosquevivo-web"
try {
    npm install
    if ($LASTEXITCODE -ne 0) {
        throw "bosquevivo-web install failed with exit code $LASTEXITCODE"
    }
    npm run build
    if ($LASTEXITCODE -ne 0) {
        throw "bosquevivo-web build failed with exit code $LASTEXITCODE"
    }
    Write-Host "[OK] bosquevivo-web built successfully" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " [OK] MVP BUILD SUCCESSFUL" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
