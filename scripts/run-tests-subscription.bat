@echo off
chcp 65001 > nul
echo ================================================================================
echo   TESTES UNITARIOS INDIVIDUAIS - SUBSCRIPTION SERVICE (INTEGRANTE 2)
echo ================================================================================

call scripts\build-all.bat
if %ERRORLEVEL% neq 0 exit /b 1

if not exist bin\mockito-extensions mkdir bin\mockito-extensions
echo mock-maker-subclass> bin\mockito-extensions\org.mockito.plugins.MockMaker

echo Executando testes unitarios de Subscription Service com JUnit 5 + Mockito...
java -javaagent:lib/byte-buddy-agent-1.15.11.jar -XX:+EnableDynamicAgentLoading -cp "bin;lib/*" com.subscriptionhub.test.TestRunner com.subscriptionhub.subscription.SubscriptionServiceTest
