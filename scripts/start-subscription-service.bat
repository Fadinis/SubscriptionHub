@echo off
chcp 65001 > nul
title Subscription Service (Porta 8082)
echo ================================================================================
echo   INICIANDO SUBSCRIPTION SERVICE - PORTA 8082
echo ================================================================================

if not exist bin\com\subscriptionhub\subscription\Main.class (
    echo Compilando projeto...
    call scripts\build-all.bat
)

java -cp "bin;lib/*" com.subscriptionhub.subscription.Main
pause

