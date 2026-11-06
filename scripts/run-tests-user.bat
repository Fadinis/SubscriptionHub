@echo off
chcp 65001 > nul
echo ================================================================================
echo   TESTES UNITARIOS INDIVIDUAIS - USER SERVICE (INTEGRANTE 1)
echo ================================================================================

call scripts\build-all.bat
if %ERRORLEVEL% neq 0 exit /b 1

echo Executando testes unitarios de User Service com JUnit 5...
java -javaagent:lib/byte-buddy-agent-1.15.11.jar -XX:+EnableDynamicAgentLoading -cp "bin;lib/*" com.subscriptionhub.test.TestRunner com.subscriptionhub.user.UserServiceTest

