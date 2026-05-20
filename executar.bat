@echo off
chcp 65001 > nul

echo ==========================================================
echo    SUBSCRIPTION HUB - GERENCIADOR DE ASSINATURAS
echo ==========================================================
echo.

:: 1. Compilar o Projeto
echo [INFO] Compilando codigo-fonte Java...
if not exist bin mkdir bin
"C:\Users\Admin\.jdks\ms-21.0.11\bin\javac.exe" -encoding UTF-8 -d bin -sourcepath src src/Main.java

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
"C:\Users\Admin\.jdks\ms-21.0.11\bin\java.exe" -cp bin Main

if %ERRORLEVEL% neq 0 (
    echo.
    echo [INFO] A aplicacao foi encerrada com codigo de saida: %ERRORLEVEL%.
)
echo.
pause
