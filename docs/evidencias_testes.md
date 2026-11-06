# 🧪 Evidências de Testes Unitários e Integração
## Subscription Hub — Sprint 07

Este documento apresenta a comprovação dos testes unitários automatizados desenvolvidos com **JUnit 5 + Mockito** e os testes de APIs REST e integração.

---

## 1. Tabela dos Testes Unitários Automatizados (12 Casos Reais)

| Microsserviço | ID | Funcionalidade / Regra de Negócio | Cenário Testado | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **User Service** | `UT-US-01` | Cadastro de Usuário | Dados válidos e e-mail inédito | Usuário registrado, ID gerado | **PASSED** ✅ |
| **User Service** | `UT-US-02` | Cadastro de Usuário | E-mail já existente na base | Rejeição com `IllegalArgumentException` | **PASSED** ✅ |
| **User Service** | `UT-US-03` | Autenticação (Login) | Credenciais corretas | Retorno do perfil do usuário autenticado | **PASSED** ✅ |
| **User Service** | `UT-US-04` | Autenticação (Login) | Senha incorreta | Retorno de `Optional.empty()` (falha) | **PASSED** ✅ |
| **Subscription Service** | `UT-SS-01` | Criação de Assinatura | Dados válidos e usuário validado no MS-1 | Assinatura salva com status "ATIVA" | **PASSED** ✅ |
| **Subscription Service** | `UT-SS-02` | Criação de Assinatura | Valor igual a zero ou negativo | Rejeição com `IllegalArgumentException` | **PASSED** ✅ |
| **Subscription Service** | `UT-SS-03` | Criação de Assinatura | Usuário inexistente (User Client mock 404) | Rejeição impedindo criação de assinatura órfã | **PASSED** ✅ |
| **Subscription Service** | `UT-SS-04` | Cancelamento de Assinatura | Assinatura ativa existente | Status alterado para "CANCELADA" | **PASSED** ✅ |
| **Notification Service** | `UT-NS-01` | Verificação de Vencimentos | Assinatura a 2 dias do vencimento (janela 3) | Alerta gerado com texto e data corretos | **PASSED** ✅ |
| **Notification Service** | `UT-NS-02` | Verificação de Vencimentos | Nenhuma assinatura vencendo na janela | Lista de alertas gerados vazia | **PASSED** ✅ |
| **Notification Service** | `UT-NS-03` | Verificação de Vencimentos | Dias de antecedência com valor negativo | Rejeição com `IllegalArgumentException` | **PASSED** ✅ |
| **Notification Service** | `UT-NS-04` | Resiliência de Comunicação | Subscription Service indisponível (falha de rede) | Tratamento amigável de erro de conectividade | **PASSED** ✅ |

---

## 2. Registro da Execução Automatizada dos Testes Unitários

Executado via comando:
```bash
scripts\run-tests.bat
```

**Log Real da Execução:**
```text
================================================================================
        SUBSCRIPTION HUB - EXECUCAO DE TESTES UNITARIOS AUTOMATIZADOS           
================================================================================

--------------------------------------------------------------------------------
  Classe de Teste: UserServiceTest
--------------------------------------------------------------------------------
  [PASSED] UT-US-01: Cadastro de usuário com dados válidos deve ter sucesso
  [PASSED] UT-US-02: Cadastro com e-mail duplicado deve lançar exceção
  [PASSED] UT-US-03: Autenticação com credenciais corretas deve retornar usuário
  [PASSED] UT-US-04: Autenticação com senha incorreta deve falhar

--------------------------------------------------------------------------------
  Classe de Teste: SubscriptionServiceTest
--------------------------------------------------------------------------------
  [PASSED] UT-SS-01: Cadastro de assinatura com dados válidos e usuário existente
  [PASSED] UT-SS-02: Cadastro com valor inválido (zero ou negativo) deve ser rejeitado
  [PASSED] UT-SS-03: Cadastro com usuário inexistente no User Service deve ser rejeitado
  [PASSED] UT-SS-04: Cancelamento de assinatura ativa deve atualizar status para CANCELADA

--------------------------------------------------------------------------------
  Classe de Teste: NotificationServiceTest
--------------------------------------------------------------------------------
  [PASSED] UT-NS-01: Verificação de vencimento gera alerta quando assinatura vence dentro da janela
  [PASSED] UT-NS-02: Verificação não gera alerta quando nenhuma assinatura vence no prazo
  [PASSED] UT-NS-03: Cálculo com dias de antecedência negativo deve ser rejeitado
  [PASSED] UT-NS-04: Resiliência de comunicação quando Subscription Service está indisponível

================================================================================
 RESUMO FINAL DA EXECUCAO DOS TESTES
================================================================================
 Total de Casos de Teste Executados: 12
 Sucessos:                           12
 Falhas:                             0
 Tempo Total:                        1264 ms
================================================================================
```

---

## 3. Roteiro e Evidências dos Testes de Integração e APIs REST (curl)

Os testes foram executados com os 3 microsserviços rodando independentemente em portas separadas:
- `User Service` na porta `8081`
- `Subscription Service` na porta `8082`
- `Notification Service` na porta `8083`

### 3.1. Consulta com Sucesso (Health e Busca por ID)
```bash
curl -s -X GET http://localhost:8081/api/users/1
```
**Resposta (HTTP 200 OK):**
```json
{"id":1,"nome":"Joao Silva","email":"joao@email.com"}
```

### 3.2. Cadastro com Sucesso no User Service
```bash
curl -s -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"nome":"Lucas Ferreira","email":"lucas@email.com","senha":"senhaSegura123"}'
```
**Resposta (HTTP 201 Created):**
```json
{"id":3,"nome":"Lucas Ferreira","email":"lucas@email.com"}
```

### 3.3. Comunicação Inter-serviços (MS-2 -> MS-1) com Sucesso
```bash
curl -s -X POST http://localhost:8082/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"nomeServico":"Amazon Prime","valor":19.90,"periodicidade":"MENSAL","categoria":"Streaming"}'
```
**Resposta (HTTP 201 Created):**
O `Subscription Service` consultou o `User Service` na porta 8081, validou que o usuário ID 1 existe e persistiu a assinatura:
```json
{"id":3,"usuarioId":1,"nomeServico":"Amazon Prime","valor":19.9,"status":"ATIVA","periodicidade":"MENSAL"}
```

### 3.4. Requisição Inválida / Erro com Código HTTP Adequado
1. **Tentativa com usuário inexistente (HTTP 422 Unprocessable Entity):**
```bash
curl -s -X POST http://localhost:8082/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":999,"nomeServico":"Teste","valor":20.0}'
```
**Resposta:**
`{"error":"Usuario nao encontrado no User Service para o ID: 999"}` (HTTP 422)

2. **Tentativa com valor negativo (HTTP 400 Bad Request):**
```bash
curl -s -X POST http://localhost:8082/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"nomeServico":"Teste","valor":-15.0}'
```
**Resposta:**
`{"error":"Valor da assinatura deve ser maior que zero."}` (HTTP 400)

3. **Tentativa de login com senha incorreta (HTTP 401 Unauthorized):**
```bash
curl -s -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","senha":"errada"}'
```
**Resposta:**
`{"error":"Credenciais invalidas"}` (HTTP 401)

