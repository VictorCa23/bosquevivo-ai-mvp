#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Apply Spotless formatting to all repositories
.DESCRIPTION
    Runs spotless:apply on all repos to auto-format code.
    Use this before committing to ensure consistent style.
.EXAMPLE
    .\scripts\format-all.ps1
#>

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
$rootDir = Split-Path -Parent $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Applying Code Formatting (Spotless)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Format core-platform
Write-Host "[1/3] Formatting core-platform..." -ForegroundColor Yellow
Push-Location "$rootDir\core-plataform"
try {
    & .\mvnw spotless:apply
    if ($LASTEXITCODE -ne 0) {
        throw "core-platform formatting failed"
    }
    Write-Host "✓ core-platform formatted" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host ""

# Format iam-service
Write-Host "[2/3] Formatting iam-service..." -ForegroundColor Yellow
Push-Location "$rootDir\iam-service"
try {
    & .\mvnw spotless:apply
    if ($LASTEXITCODE -ne 0) {
        throw "iam-service formatting failed"
    }
    Write-Host "✓ iam-service formatted" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host ""

# Format ai-service
Write-Host "[3/3] Formatting ai-service..." -ForegroundColor Yellow
Push-Location "$rootDir\ai-service"
try {
    & .\mvnw.cmd spotless:apply
    if ($LASTEXITCODE -ne 0) {
        throw "ai-service formatting failed"
    }
    Write-Host "✓ ai-service formatted" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ✓ ALL CODE FORMATTED" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
