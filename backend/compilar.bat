@echo off
echo ============================================
echo   Compilando Help Desk API...
echo ============================================

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist bin mkdir bin

dir /s /b src\*.java > sources.txt

javac -encoding UTF-8 -d bin @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilacao concluida com sucesso!
    del sources.txt
) else (
    echo.
    echo ERRO na compilacao!
)

pause