@echo off
chcp 65001 > nul
title User Service (Porta 8081)
echo ================================================================================
echo   INICIANDO USER SERVICE - PORTA 8081
echo ================================================================================

if not exist bin\com\subscriptionhub\user\Main.class (
    echo Compilando projeto...
    call scripts\build-all.bat
)

java -cp "bin;lib/*" com.subscriptionhub.user.Main
pause

