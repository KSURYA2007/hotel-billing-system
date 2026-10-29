Write-Host "Compiling Restaurant Management System..." -ForegroundColor Cyan
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }

$javaFiles = (Get-ChildItem -Recurse -Filter *.java -Path src).FullName
javac -cp "lib/*" -d bin $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "Starting Application..." -ForegroundColor Green
    java -cp "bin;lib/*" comp.rest.Main
} else {
    Write-Host "Compilation failed!" -ForegroundColor Red
}
