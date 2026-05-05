@echo off
echo Compiling Clinic Management System - Sprint 3...
if not exist bin mkdir bin
if not exist lib mkdir lib
javac -cp "lib/*" -d bin src\cms\*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Compilation successful. Launching...
java -cp "bin;lib/*" cms.MainFrame
pause
