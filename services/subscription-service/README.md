# 📋 Subscription Service (Microsserviço 2)

## 📌 Responsabilidade
Gerenciamento do ciclo de vida das assinaturas (criação, cancelamento, alteração, consulta) com validação de usuários via comunicação REST com o **User Service**.

- **Porta padrão:** `8082`
- **Base de Dados:** Isolada em `data/subscriptions.json`

## 🔗 Comunicação Inter-serviços
- Antes de salvar uma nova assinatura, este microsserviço dispara uma requisição HTTP REST `GET http://localhost:8081/api/users/{usuarioId}` para o **User Service**.
- Caso o usuário não exista, a operação é rejeitada com código HTTP `422 Unprocessable Entity`.
- Caso o User Service esteja indisponível, retorna código HTTP `503 Service Unavailable`.

## 🚀 Endpoints da API REST

| Método | Endpoint | Descrição | Exemplo de Sucesso (Status) |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/subscriptions/health` | Healthcheck do serviço | `200 OK` |
| `GET` | `/api/subscriptions` | Listar todas as assinaturas | `200 OK` |
| `GET` | `/api/subscriptions/user/{usuarioId}` | Assinaturas de um usuário específico | `200 OK` |
| `GET` | `/api/subscriptions/{id}` | Buscar assinatura por ID | `200 OK` ou `404 Not Found` |
| `POST` | `/api/subscriptions` | Cadastrar nova assinatura (com consulta ao MS-1) | `201 Created` ou `400 / 422 / 503` |
| `PUT` | `/api/subscriptions/{id}/cancel` | Cancelar uma assinatura ativa | `200 OK` ou `404 Not Found` |

## 🧪 Testes Unitários Automatizados
Implementados em `src/test/java/com/subscriptionhub/subscription/SubscriptionServiceTest.java` com **JUnit 5** e **Mockito**:
- `UT-SS-01`: Cadastro de assinatura com sucesso (mockando UserClient).
- `UT-SS-02`: Rejeição de cadastro com valor inválido (<= 0).
- `UT-SS-03`: Rejeição de cadastro quando usuário não existe (mock 404).
- `UT-SS-04`: Cancelamento de assinatura ativa.

