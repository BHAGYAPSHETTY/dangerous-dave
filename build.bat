@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo   BUILDING DANGEROUS DAVE (JAVA 17)
echo ========================================================

REM Locate javac
set JAVAC=javac
where javac >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\Java\jdk-17\bin\javac.exe" (
        set JAVAC="C:\Program Files\Java\jdk-17\bin\javac.exe"
    ) else (
        echo ERROR: javac compiler not found! Please ensure JDK 17 is installed.
        exit /b 1
    )
)

REM Locate jar
set JAR=jar
where jar >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\Java\jdk-17\bin\jar.exe" (
        set JAR="C:\Program Files\Java\jdk-17\bin\jar.exe"
    )
)

REM Locate java
set JAVA=java
where java >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
        set JAVA="C:\Program Files\Java\jdk-17\bin\java.exe"
    )
)

if not exist out mkdir out

echo [1/3] Compiling Java source files...
%JAVAC% -d out -sourcepath src\main\java;src\test\java src\main\java\com\dave\Main.java src\main\java\com\dave\core\*.java src\main\java\com\dave\model\*.java src\main\java\com\dave\physics\*.java src\main\java\com\dave\levels\*.java src\main\java\com\dave\gfx\*.java src\main\java\com\dave\audio\*.java src\main\java\com\dave\ui\*.java src\main\java\com\dave\ui\screens\*.java src\test\java\com\dave\test\*.java
if errorlevel 1 (
    echo Compilation FAILED!
    exit /b 1
)

echo [2/3] Running automated regression and anti-exploit test suite...
%JAVA% -cp out com.dave.test.TestRunner
if errorlevel 1 (
    echo Automated tests FAILED!
    exit /b 1
)

echo [3/3] Packaging DangerousDave.jar...
%JAR% cfe DangerousDave.jar com.dave.Main -C out com
if errorlevel 1 (
    echo JAR packaging FAILED!
    exit /b 1
)

echo.
echo ========================================================
echo   BUILD SUCCESSFUL! Run with: run.bat or java -jar DangerousDave.jar
echo ========================================================
