@echo off
set JAVA=java
where java >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
        set JAVA="C:\Program Files\Java\jdk-17\bin\java.exe"
    )
)

if exist DangerousDave.jar (
    %JAVA% -jar DangerousDave.jar
) else if exist out (
    %JAVA% -cp out com.dave.Main
) else (
    echo Project not built yet! Running build.bat first...
    call build.bat
    if exist DangerousDave.jar (
        %JAVA% -jar DangerousDave.jar
    )
)
