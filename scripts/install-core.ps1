#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Install core-platform to local Maven repository
.DESCRIPTION
    Convenience script to quickly install core-platform dependency.
    Run this whenever core-platform changes before running services.
.PARAMETER SkipTests
    Skip test execution (default: true for speed)
.EXAMPLE
    .\scripts\install-core.ps1
.EXAMPLE
    .\scripts\install-core.ps1 -SkipTests:$false
#>

[CmdletBinding()]
param(
    [switch]$SkipTests = $true
)

$ErrorActionPreference = "Stop"
$rootDir = Split-Path -Parent $PSScriptRoot

Write-Host "Installing core-platform to local Maven repo..." -ForegroundColor Yellow

$testFlag = if ($SkipTests) { "-DskipTests" } else { "" }

Push-Location "$rootDir\core-plataform"
try {
    & .\mvnw.cmd clean install $testFlag
    if ($LASTEXITCODE -ne 0) {
        throw "core-platform install failed"
    }
    Write-Host "[OK] core-platform installed successfully" -ForegroundColor Green
} finally {
    Pop-Location
}
