@echo off
chcp 65001 > nul
echo ================================================================================
echo   SUBSCRIPTION HUB - EXECUTOR DE TESTES UNITARIOS AUTOMATIZADOS (JUNIT 5)
echo ================================================================================

call scripts\build-all.bat
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Nao foi possivel rodar os testes devido a erros de compilacao.
    exit /b 1
)

echo.
echo Executando testes unitarios automatizados...
echo.

java -javaagent:lib/byte-buddy-agent-1.15.11.jar -XX:+EnableDynamicAgentLoading -cp "bin;lib/*" com.subscriptionhub.test.TestRunner

if %ERRORLEVEL% equ 0 (
    echo [SUCESSO] Todos os 12 testes unitarios foram executados e APROVADOS!
) else (
    echo [FALHA] Alguns testes unitarios falharam.
    exit /b 1
)

