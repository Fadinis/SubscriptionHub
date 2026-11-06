@echo off
chcp 65001 > nul
echo ================================================================================
echo   SUBSCRIPTION HUB - TESTES DAS APIS REST E INTEGRACAO (PARTE IV DO EDITAL)
echo ================================================================================
echo Certifique-se de que os servicos foram iniciados com scripts\start-all.bat antes!
echo.

echo --------------------------------------------------------------------------------
echo [1/9] Healthcheck dos 3 Microsservicos (Verificando se estao ativos)
echo --------------------------------------------------------------------------------
echo [MS-1] User Service (8081):
curl -s -X GET http://localhost:8081/api/users/health
echo.
echo [MS-2] Subscription Service (8082):
curl -s -X GET http://localhost:8082/api/subscriptions/health
echo.
echo [MS-3] Notification Service (8083):
curl -s -X GET http://localhost:8083/api/notifications/health
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [2/9] Operacao de Consulta com Sucesso (GET /api/users/1)
echo --------------------------------------------------------------------------------
curl -s -X GET http://localhost:8081/api/users/1
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [3/9] Operacao de Cadastro de Usuario com Sucesso (POST /api/users)
echo --------------------------------------------------------------------------------
curl -s -X POST http://localhost:8081/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Lucas Ferreira\",\"email\":\"lucas.ferreira@email.com\",\"senha\":\"senhaForte123\"}"
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [4/9] Operacao de Autenticacao / Login (POST /api/users/login)
echo --------------------------------------------------------------------------------
echo Testando login com credenciais validas:
curl -s -X POST http://localhost:8081/api/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"joao@email.com\",\"senha\":\"senha123\"}"
echo.
echo Testando login com senha incorreta (Esperado HTTP 401 Unauthorized):
curl -s -X POST http://localhost:8081/api/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"joao@email.com\",\"senha\":\"senhaErrada\"}"
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [5/9] Integracao MS-2 -> MS-1: Cadastro de Assinatura com Validacao do Usuario
echo --------------------------------------------------------------------------------
echo Cadastrando assinatura para o usuario ID 1 (Subscription Service consulta User Service via REST):
curl -s -X POST http://localhost:8082/api/subscriptions ^
  -H "Content-Type: application/json" ^
  -d "{\"usuarioId\":1,\"nomeServico\":\"Amazon Prime Video\",\"valor\":19.90,\"periodicidade\":\"MENSAL\",\"categoria\":\"Streaming\"}"
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [6/9] Tratamento de Erro de Integracao: Usuario Inexistente (Esperado HTTP 422)
echo --------------------------------------------------------------------------------
echo Tentando cadastrar assinatura para usuario 9999 (User Service responde 404, Subscription rejeita 422):
curl -s -X POST http://localhost:8082/api/subscriptions ^
  -H "Content-Type: application/json" ^
  -d "{\"usuarioId\":9999,\"nomeServico\":\"Servico Invalido\",\"valor\":50.00,\"periodicidade\":\"MENSAL\",\"categoria\":\"Outros\"}"
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [7/9] Requisicao Invalida com Codigo HTTP Adequado: Valor <= 0 (Esperado HTTP 400)
echo --------------------------------------------------------------------------------
curl -s -X POST http://localhost:8082/api/subscriptions ^
  -H "Content-Type: application/json" ^
  -d "{\"usuarioId\":1,\"nomeServico\":\"Plano Invalido\",\"valor\":-20.00,\"periodicidade\":\"MENSAL\",\"categoria\":\"Outros\"}"
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [8/9] Integracao MS-3 -> MS-2: Notification Service checa vencimentos no MS-2
echo --------------------------------------------------------------------------------
echo Disparando checagem de vencimento de assinaturas para o usuario 1 (janela de 5 dias):
curl -s -X POST http://localhost:8083/api/notifications/check ^
  -H "Content-Type: application/json" ^
  -d "{\"usuarioId\":1,\"diasAntecedencia\":5}"
echo.
echo Consultando alertas gerados do usuario 1:
curl -s -X GET http://localhost:8083/api/notifications/user/1
echo.
pause

echo.
echo --------------------------------------------------------------------------------
echo [9/9] Operacao de Alteracao de Estado: Cancelamento de Assinatura (PUT .../cancel)
echo --------------------------------------------------------------------------------
curl -s -X PUT http://localhost:8082/api/subscriptions/1/cancel
echo.
echo ================================================================================
echo   FIM DOS TESTES DE INTEGRACAO E APIS REST!
echo ================================================================================
pause

