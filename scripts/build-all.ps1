#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Build all repositories in correct order with dependency resolution
.DESCRIPTION
    Builds core-platform first (install to local Maven repo), then services.
    This ensures iam-service and ai-service can resolve core-platform dependency.
.PARAMETER SkipTests
    Skip test execution (default: false)
.PARAMETER Clean
    Run clean before build (default: true)
.EXAMPLE
    .\scripts\build-all.ps1
.EXAMPLE
    .\scripts\build-all.ps1 -SkipTests
#>

[CmdletBinding()]
param(
    [switch]$SkipTests = $false,
    [switch]$Clean = $true
)

$ErrorActionPreference = "Stop"
$rootDir = Split-Path -Parent $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Multi-Repo Build Workflow" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$testFlag = if ($SkipTests) { "-DskipTests" } else { "" }
$cleanPhase = if ($Clean) { "clean" } else { "" }

# Step 1: Build and install core-platform
Write-Host "[1/3] Building core-platform (install to local Maven repo)..." -ForegroundColor Yellow
Push-Location "$rootDir\core-plataform"
try {
    & .\mvnw.cmd $cleanPhase install $testFlag
    if ($LASTEXITCODE -ne 0) {
        throw "core-platform build failed with exit code $LASTEXITCODE"
    }
    Write-Host "[OK] core-platform installed successfully" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host ""

# Step 2: Build iam-service
Write-Host "[2/3] Building iam-service..." -ForegroundColor Yellow
Push-Location "$rootDir\iam-service"
try {
    & .\mvnw.cmd $cleanPhase verify $testFlag
    if ($LASTEXITCODE -ne 0) {
        throw "iam-service build failed with exit code $LASTEXITCODE"
    }
    Write-Host "[OK] iam-service built successfully" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host ""

# Step 3: Build ai-service
Write-Host "[3/3] Building ai-service..." -ForegroundColor Yellow
Push-Location "$rootDir\ai-service"
try {
    & .\mvnw.cmd $cleanPhase verify $testFlag
    if ($LASTEXITCODE -ne 0) {
        throw "ai-service build failed with exit code $LASTEXITCODE"
    }
    Write-Host "[OK] ai-service built successfully" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " [OK] ALL BUILDS SUCCESSFUL" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
