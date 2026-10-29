@echo off
echo Compiling Restaurant Management System...
if not exist bin mkdir bin
javac -cp "lib/*" -d bin src/comp/rest/models/*.java src/comp/rest/bill/*.java src/comp/rest/payment/*.java src/comp/rest/config/*.java src/comp/rest/repository/*.java src/comp/rest/services/*.java src/comp/rest/CLI/*.java src/comp/rest/*.java

if %ERRORLEVEL% EQU 0 (
    echo Starting Application...
    java -cp "bin;lib/*" comp.rest.Main
) else (
    echo Compilation failed!
)
pause
