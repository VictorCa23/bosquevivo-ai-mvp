# Run Maven tests (mvnw -q -B test).
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

& .\mvnw.cmd -q -B test
