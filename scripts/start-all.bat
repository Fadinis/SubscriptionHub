@echo off
chcp 65001 > nul
echo ================================================================================
echo   INICIALIZANDO OS 3 MICROSSERVICOS DO SUBSCRIPTION HUB (JANELAS SEPARADAS)
echo ================================================================================

call scripts\build-all.bat
if %ERRORLEVEL% neq 0 (
    echo [ERRO] Falha ao compilar. Abortando inicializacao.
    pause
    exit /b 1
)

echo Iniciando User Service (porta 8081)...
start "User Service (8081)" cmd /k "scripts\start-user-service.bat"

timeout /t 2 /nobreak > nul

echo Iniciando Subscription Service (porta 8082)...
start "Subscription Service (8082)" cmd /k "scripts\start-subscription-service.bat"

timeout /t 2 /nobreak > nul

echo Iniciando Notification Service (porta 8083)...
start "Notification Service (8083)" cmd /k "scripts\start-notification-service.bat"

echo.
echo ================================================================================
echo  Todos os 3 microsservicos foram disparados em terminais independentes!
echo  - User Service:         http://localhost:8081/api/users/health
echo  - Subscription Service: http://localhost:8082/api/subscriptions/health
echo  - Notification Service: http://localhost:8083/api/notifications/health
echo ================================================================================

