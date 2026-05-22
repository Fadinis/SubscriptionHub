@echo off
chcp 65001 > nul

:: Localizar javac e java
set JAVAC_CMD=javac
set JAVA_CMD=java
where javac >nul 2>nul
if %ERRORLEVEL% neq 0 (
    for /d %%D in ("%USERPROFILE%\.jdks\*") do (
        if exist "%%D\bin\javac.exe" (
            set JAVAC_CMD="%%D\bin\javac.exe"
            set JAVA_CMD="%%D\bin\java.exe"
        )
    )
)

echo ==========================================================
echo    SUBSCRIPTION HUB - GERENCIADOR DE ASSINATURAS
echo ==========================================================
echo.

:: 1. Compilar o Projeto
echo [INFO] Compilando codigo-fonte Java...
if not exist bin mkdir bin
%JAVAC_CMD% -encoding UTF-8 -d bin -sourcepath src src/Main.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERRO] Houve falha na compilacao do projeto.
    echo.
    pause
    exit /b %ERRORLEVEL%
)

echo [INFO] Compilacao concluida com sucesso!
echo.

:: 2. Executar a GUI
echo [INFO] Iniciando a interface grafica...
%JAVA_CMD% -cp bin Main

if %ERRORLEVEL% neq 0 (
    echo.
    echo [INFO] A aplicacao foi encerrada com codigo de saida: %ERRORLEVEL%.
)
echo.
pause
