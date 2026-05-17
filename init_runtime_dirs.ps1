<#
  init_runtime_dirs.ps1
  Crea estructura enterprise de runtime (logs/pids/tmp/dumps) para el workspace SolverIA.
  Uso:
    PS> .\init_runtime_dirs.ps1
    PS> .\init_runtime_dirs.ps1 -WorkspaceRoot "C:\ProyectoAI"
#>

[CmdletBinding()]
param(
  [string]$WorkspaceRoot = (Get-Location).Path
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Ensure-Dir([string]$Path) {
  if (-not (Test-Path -LiteralPath $Path)) {
    New-Item -ItemType Directory -Path $Path | Out-Null
  }
}

function Info($msg) { Write-Host "[INFO] $msg" }
function Ok($msg)   { Write-Host "[OK]  $msg" }

$WorkspaceRoot = (Resolve-Path $WorkspaceRoot).Path

# Detectar si estamos dentro de un repo y subir al root del workspace si existen los 3 repos como siblings.
function Find-WorkspaceRoot([string]$StartDir) {
  $dir = (Resolve-Path $StartDir).Path
  for ($i = 0; $i -lt 8; $i++) {
    $ai  = Join-Path $dir "ai-service"
    $iam = Join-Path $dir "iam-service"
    $core = Join-Path $dir "core-plataform"
    if ((Test-Path $ai) -and (Test-Path $iam) -and (Test-Path $core)) { return $dir }
    $parent = Split-Path $dir -Parent
    if ($parent -eq $dir) { break }
    $dir = $parent
  }
  return $StartDir
}

$WorkspaceRoot = Find-WorkspaceRoot $WorkspaceRoot

Info "WorkspaceRoot = $WorkspaceRoot"

$runtime = Join-Path $WorkspaceRoot ".runtime"
$logs    = Join-Path $runtime "logs"

$dirs = @(
  $runtime,
  $logs,
  (Join-Path $logs "ai-service"),
  (Join-Path $logs "iam-service"),
  (Join-Path $logs "core-plataform"),
  (Join-Path $runtime "pids"),
  (Join-Path $runtime "tmp"),
  (Join-Path $runtime "dumps"),
  (Join-Path $runtime "reports"),
  (Join-Path $runtime "archives")
)

foreach ($d in $dirs) { Ensure-Dir $d }

Ok "Runtime dirs creados."

# Sugerencia: agregar .runtime/ a .gitignore de cada repo si no está
$repos = @("ai-service", "iam-service", "core-plataform")
foreach ($r in $repos) {
  $repoPath = Join-Path $WorkspaceRoot $r
  $gitignore = Join-Path $repoPath ".gitignore"
  if (Test-Path $gitignore) {
    $content = Get-Content $gitignore -Raw
    if ($content -notmatch "(?m)^\s*\.runtime/") {
      Add-Content -Path $gitignore -Value "`r`n# runtime artifacts (local)`r`n.runtime/`r`n*.log`r`nlogs/`r`n"
      Ok "Actualizado .gitignore en $r (agregado .runtime/, logs/, *.log)"
    } else {
      Info ".gitignore ya contiene .runtime/ en $r"
    }
  } else {
    Info "No existe .gitignore en $repoPath (saltando)"
  }
}

Ok "Listo."
