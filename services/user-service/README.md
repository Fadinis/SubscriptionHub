# 👤 User Service (Microsserviço 1)

## 📌 Responsabilidade
Gerenciamento de contas de usuário, autenticação e validação de existência de usuários para outros microsserviços.

- **Porta padrão:** `8081`
- **Base de Dados:** Isolada em `data/users.json`

## 🚀 Endpoints da API REST

| Método | Endpoint | Descrição | Exemplo de Sucesso (Status) |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/users/health` | Healthcheck do serviço | `200 OK` |
| `GET` | `/api/users` | Listar todos os usuários | `200 OK` |
| `GET` | `/api/users/{id}` | Buscar dados de um usuário | `200 OK` ou `404 Not Found` |
| `POST` | `/api/users` | Cadastrar novo usuário | `201 Created` ou `400 / 409` |
| `POST` | `/api/users/login` | Autenticar usuário | `200 OK` ou `401 Unauthorized` |

## 🧪 Testes Unitários Automatizados
Implementados em `src/test/java/com/subscriptionhub/user/UserServiceTest.java` com **JUnit 5** e **Mockito**:
- `UT-US-01`: Cadastro de usuário com dados válidos.
- `UT-US-02`: Rejeição de cadastro com e-mail duplicado.
- `UT-US-03`: Autenticação com credenciais corretas.
- `UT-US-04`: Falha de autenticação com senha incorreta.

