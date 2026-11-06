@echo off
chcp 65001 > nul
title Notification Service (Porta 8083)
echo ================================================================================
echo   INICIANDO NOTIFICATION SERVICE - PORTA 8083
echo ================================================================================

if not exist bin\com\subscriptionhub\notification\Main.class (
    echo Compilando projeto...
    call scripts\build-all.bat
)

java -cp "bin;lib/*" com.subscriptionhub.notification.Main
pause

