@echo off
title Dangerous Dave (1990) - Remastered

echo ========================================
echo   DANGEROUS DAVE - REMASTERED
echo ========================================
echo.
echo Checking for Java installation...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed!
    echo.
    echo Please install Java 17 or higher from:
    echo https://www.oracle.com/java/technologies/downloads/
    echo.
    echo Or download from:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo [OK] Java is installed!
echo.
echo Starting game...
echo.

REM Navigate to the folder where this batch file is located
cd /d "%~dp0"

REM Run the game
java -jar DangerousDave.jar

REM If the game closes, pause so user can see any errors
echo.
echo Game closed. Press any key to exit...
pause >nul