# 🚀 Subscription Hub — Arquitetura de Microsserviços
**Disciplina:** Práticas de Arquitetura e Engenharia de Software (PAES) — 6º Semestre  
**Sprint 07:** Arquitetura, Implementação e Testes de Microsserviços  

---

## 📌 Visão Geral do Projeto

O **Subscription Hub** é um sistema para gestão financeira e controle de assinaturas recorrentes (streamings, softwares SaaS, academias, planos recorrentes). 

Nesta Sprint 07, o projeto evoluiu para uma **Arquitetura de Microsserviços**, com separação clara de responsabilidades, persistência isolada por serviço (*Database-per-Service*), comunicação síncrona via APIs REST e testes unitários automatizados com **JUnit 5 + Mockito**.

---

## 🏗️ Microsserviços Implementados

| Microsserviço | Porta | Responsabilidade | Base de Dados |
| :--- | :---: | :--- | :--- |
| **User Service** | `8081` | Gerenciamento de contas, autenticação e perfil do usuário | `services/user-service/data/users.json` |
| **Subscription Service** | `8082` | CRUD de assinaturas e validação de usuário via REST com o MS-1 | `services/subscription-service/data/subscriptions.json` |
| **Notification Service** | `8083` | Regras de alerta de vencimento com consulta via REST ao MS-2 | `services/notification-service/data/notifications.json` |

---

## 📂 Organização do Repositório

```text
SubscriptionHub/
├── bin/                        # Bytecodes Java compilados
├── lib/                        # Dependências oficiais (JUnit 5, Mockito, Jackson, ByteBuddy)
├── services/
│   ├── user-service/           # MS-1: Gestão de Usuários e Login (Porta 8081)
│   ├── subscription-service/   # MS-2: Gestão de Assinaturas (Porta 8082)
│   └── notification-service/   # MS-3: Alertas e Vencimentos (Porta 8083)
├── scripts/
│   ├── build-all.bat           # Compilação unificada de todos os serviços e testes
│   ├── run-tests.bat           # Executa os 12 testes unitários automatizados (JUnit 5)
│   ├── start-all.bat           # Inicializa os 3 microsserviços em janelas independentes
│   ├── start-user-service.bat
│   ├── start-subscription-service.bat
│   ├── start-notification-service.bat
│   └── test-apis.bat           # Bateria de testes de integração com curl
├── docs/
│   ├── arquitetura.md          # Diagrama completo, contexto e justificativas técnicas
│   ├── apis.md                 # Especificação detalhada de endpoints, payloads e responses
│   ├── evidencias_testes.md    # Logs de execução dos testes unitários e APIs REST
│   └── guia_equipe_git_jira.md # Guia de trabalho em equipe com Git Flow e Jira
├── PLANO_SPRINT_07.md          # Plano formal de execução da Sprint 07
└── README.md                   # Este arquivo
```

---

## ⚡ Como Executar o Projeto (Guia Rápido)

### Pré-requisitos
- **Java JDK 21** ou superior instalado e configurado no `PATH`.

### 1. Compilar o Projeto
Abra o prompt de comando ou terminal na raiz do projeto e execute:
```cmd
scripts\build-all.bat
```

---

### 2. Executar os Testes Unitários Automatizados (12 Casos de Teste)
Para rodar os testes unitários com **JUnit 5** e **Mockito**:
```cmd
scripts\run-tests.bat
```
> **Resultado:** Execução automatizada dos 12 testes cobrindo regras de negócio, validações de valores negativos, e-mails duplicados, mocks de clientes REST e tolerância a falhas.

---

### 3. Inicializar os 3 Microsserviços
Para iniciar os 3 microsserviços simultaneamente em terminais separados:
```cmd
scripts\start-all.bat
```
Ou individualmente:
- User Service: `scripts\start-user-service.bat` (Porta `8081`)
- Subscription Service: `scripts\start-subscription-service.bat` (Porta `8082`)
- Notification Service: `scripts\start-notification-service.bat` (Porta `8083`)

---

### 4. Executar os Testes das APIs REST e Integração
Com os três serviços em execução, execute o script demonstrativo:
```cmd
scripts\test-apis.bat
```
O script testará:
1. Healthcheck dos 3 serviços (`200 OK`).
2. Consulta de usuário (`200 OK`).
3. Cadastro de usuário com validação de formato (`201 Created`).
4. Autenticação e rejeição por senha inválida (`401 Unauthorized`).
5. **Comunicação REST Inter-serviços (MS-2 -> MS-1):** Cadastro de assinatura com validação síncrona do usuário.
6. **Tratamento de erro de integração:** Rejeição ao tentar cadastrar para usuário inexistente (`422 Unprocessable Entity`).
7. **Rejeição por regra de negócio:** Assinatura com valor <= 0 (`400 Bad Request`).
8. **Comunicação REST Inter-serviços (MS-3 -> MS-2):** Disparo de verificação de vencimentos gerando alertas.
9. Cancelamento de assinatura ativa (`200 OK`).

---

## 📖 Documentação Detalhada
- 🏛️ [Arquitetura e Justificativa Técnica](file:///docs/arquitetura.md)
- 📡 [Contratos das APIs REST](file:///docs/apis.md)
- 🧪 [Evidências dos Testes Unitários e REST](file:///docs/evidencias_testes.md)
- 👥 [Guia de Trabalho em Equipe no Git e Jira](file:///docs/guia_equipe_git_jira.md)
- 📋 [Plano de Execução da Sprint 07](file:///PLANO_SPRINT_07.md)

