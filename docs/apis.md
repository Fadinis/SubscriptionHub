# 📡 Documentação das APIs REST
## Subscription Hub — Catálogo de Endpoints

Esta documentação especifica os contratos de API REST dos 3 microsserviços implementados na Sprint 07.  
Todas as requisições e respostas utilizam o cabeçalho `Content-Type: application/json; charset=UTF-8`.

---

## 👤 1. User Service (Porta 8081)

### 1.1. Healthcheck
- **Método:** `GET`
- **Endpoint:** `/api/users/health`
- **Finalidade:** Verificar a disponibilidade do serviço.
- **Códigos de Resposta:** `200 OK`
- **Exemplo de Resposta:**
```json
{
  "status": "UP",
  "service": "user-service",
  "port": 8081
}
```

### 1.2. Cadastrar Novo Usuário
- **Método:** `POST`
- **Endpoint:** `/api/users`
- **Finalidade:** Registrar um novo usuário no sistema.
- **Corpo da Requisição (JSON):**
```json
{
  "nome": "Carlos Andrade",
  "email": "carlos@email.com",
  "senha": "senhaForte123"
}
```
- **Códigos de Resposta:**
  - `201 Created`: Usuário registrado com sucesso.
  - `400 Bad Request`: Dados obrigatórios ausentes ou senha curta (< 4 caracteres).
  - `409 Conflict`: E-mail já cadastrado na base.
- **Exemplo de Resposta (201):**
```json
{
  "id": 3,
  "nome": "Carlos Andrade",
  "email": "carlos@email.com"
}
```

### 1.3. Autenticação de Usuário (Login)
- **Método:** `POST`
- **Endpoint:** `/api/users/login`
- **Finalidade:** Validar credenciais de acesso.
- **Corpo da Requisição (JSON):**
```json
{
  "email": "carlos@email.com",
  "senha": "senhaForte123"
}
```
- **Códigos de Resposta:**
  - `200 OK`: Credenciais válidas.
  - `401 Unauthorized`: E-mail ou senha incorretos.
  - `400 Bad Request`: Corpo malformatado.
- **Exemplo de Resposta (200):**
```json
{
  "mensagem": "Autenticado com sucesso",
  "usuario": {
    "id": 3,
    "nome": "Carlos Andrade",
    "email": "carlos@email.com"
  }
}
```

### 1.4. Buscar Usuário por ID
- **Método:** `GET`
- **Endpoint:** `/api/users/{id}`
- **Finalidade:** Retornar dados públicos do perfil do usuário (usado pelo Subscription Service).
- **Códigos de Resposta:**
  - `200 OK`: Encontrado.
  - `404 Not Found`: ID não existente.
- **Exemplo de Resposta (200):**
```json
{
  "id": 1,
  "nome": "Joao Silva",
  "email": "joao@email.com"
}
```

---

## 📋 2. Subscription Service (Porta 8082)

### 2.1. Healthcheck
- **Método:** `GET`
- **Endpoint:** `/api/subscriptions/health`
- **Códigos de Resposta:** `200 OK`

### 2.2. Cadastrar Assinatura (com Validação Inter-serviços)
- **Método:** `POST`
- **Endpoint:** `/api/subscriptions`
- **Finalidade:** Cadastrar nova assinatura, validando a existência do usuário via chamada REST ao User Service (`GET /api/users/{id}`).
- **Corpo da Requisição (JSON):**
```json
{
  "usuarioId": 1,
  "nomeServico": "Netflix Premium",
  "valor": 55.90,
  "periodicidade": "MENSAL",
  "categoria": "Streaming"
}
```
- **Códigos de Resposta:**
  - `201 Created`: Assinatura criada com sucesso.
  - `400 Bad Request`: Valor <= 0 ou campos vazios.
  - `422 Unprocessable Entity`: Usuário não encontrado no User Service.
  - `503 Service Unavailable`: User Service inacessível / fora do ar.
- **Exemplo de Resposta (201):**
```json
{
  "id": 3,
  "usuarioId": 1,
  "nomeServico": "Netflix Premium",
  "valor": 55.9,
  "dataVencimento": 1733575200000,
  "status": "ATIVA",
  "categoria": "Streaming",
  "periodicidade": "MENSAL"
}
```

### 2.3. Consultar Assinaturas de um Usuário
- **Método:** `GET`
- **Endpoint:** `/api/subscriptions/user/{usuarioId}`
- **Finalidade:** Listar todas as assinaturas vinculadas a um usuário (consumido pelo Notification Service).
- **Códigos de Resposta:** `200 OK`
- **Exemplo de Resposta (200):**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "nomeServico": "Netflix Premium",
    "valor": 55.9,
    "status": "ATIVA",
    "periodicidade": "MENSAL"
  }
]
```

### 2.4. Cancelar Assinatura
- **Método:** `PUT`
- **Endpoint:** `/api/subscriptions/{id}/cancel`
- **Finalidade:** Alterar o status da assinatura para `CANCELADA`.
- **Códigos de Resposta:**
  - `200 OK`: Cancelamento efetuado.
  - `404 Not Found`: Assinatura inexistente.
- **Exemplo de Resposta (200):**
```json
{
  "mensagem": "Assinatura cancelada com sucesso",
  "assinatura": {
    "id": 1,
    "status": "CANCELADA"
  }
}
```

---

## 🔔 3. Notification Service (Porta 8083)

### 3.1. Healthcheck
- **Método:** `GET`
- **Endpoint:** `/api/notifications/health`
- **Códigos de Resposta:** `200 OK`

### 3.2. Disparar Checagem de Vencimentos (com Consulta ao MS-2)
- **Método:** `POST`
- **Endpoint:** `/api/notifications/check`
- **Finalidade:** Acionar rotina de verificação de assinaturas próximas do vencimento para um usuário, consumindo o Subscription Service via REST (`GET /api/subscriptions/user/{id}`).
- **Corpo da Requisição (JSON):**
```json
{
  "usuarioId": 1,
  "diasAntecedencia": 5
}
```
- **Códigos de Resposta:**
  - `200 OK`: Verificação concluída e lista de alertas gerados.
  - `400 Bad Request`: Dias de antecedência negativo.
  - `503 Service Unavailable`: Subscription Service fora do ar.
- **Exemplo de Resposta (200):**
```json
{
  "mensagem": "Verificacao concluida com sucesso",
  "totalGerados": 1,
  "alertas": [
    {
      "id": 1,
      "usuarioId": 1,
      "assinaturaId": 1,
      "mensagem": "Alerta de Vencimento: A assinatura 'Netflix Premium' (R$ 55.90) vence em 2 dia(s)!",
      "dataCriacao": "2026-11-06T10:40:00",
      "lida": false
    }
  ]
}
```

### 3.3. Listar Notificações do Usuário
- **Método:** `GET`
- **Endpoint:** `/api/notifications/user/{usuarioId}`
- **Códigos de Resposta:** `200 OK`

### 3.4. Marcar Notificação como Lida
- **Método:** `PUT`
- **Endpoint:** `/api/notifications/{id}/read`
- **Códigos de Resposta:**
  - `200 OK`: Marcada como lida.
  - `404 Not Found`: Notificação não encontrada.

