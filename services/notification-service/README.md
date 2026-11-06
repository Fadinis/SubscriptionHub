# 🔔 Notification Service (Microsserviço 3)

## 📌 Responsabilidade
Gestão e cálculo de alertas de vencimento para os usuários a partir da consulta de assinaturas ativas no **Subscription Service**.

- **Porta padrão:** `8083`
- **Base de Dados:** Isolada em `data/notifications.json`

## 🔗 Comunicação Inter-serviços
- Durante a verificação de vencimentos (`POST /api/notifications/check`), este microsserviço dispara uma requisição HTTP REST `GET http://localhost:8082/api/subscriptions/user/{usuarioId}` para o **Subscription Service**.
- Calcula dinamicamente quais assinaturas vencem dentro da janela de antecedência informada e registra os alertas.

## 🚀 Endpoints da API REST

| Método | Endpoint | Descrição | Exemplo de Sucesso (Status) |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/notifications/health` | Healthcheck do serviço | `200 OK` |
| `GET` | `/api/notifications` | Listar todos os alertas | `200 OK` |
| `GET` | `/api/notifications/user/{usuarioId}` | Notificações de um usuário | `200 OK` |
| `POST` | `/api/notifications/check` | Disparar verificação de vencimentos (via MS-2) | `200 OK` ou `400 / 503` |
| `PUT` | `/api/notifications/{id}/read` | Marcar notificação como lida | `200 OK` ou `404 Not Found` |

## 🧪 Testes Unitários Automatizados
Implementados em `src/test/java/com/subscriptionhub/notification/NotificationServiceTest.java` com **JUnit 5** e **Mockito**:
- `UT-NS-01`: Geração de alerta para assinatura vencendo dentro da janela de antecedência.
- `UT-NS-02`: Não geração de alerta quando nenhuma assinatura vence no prazo.
- `UT-NS-03`: Rejeição de parâmetro de dias de antecedência negativo.
- `UT-NS-04`: Resiliência em caso de falha de conexão com o Subscription Service.

