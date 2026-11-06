# 🎯 Backlog Detalhado do Jira — Sprint 07
## Subscription Hub: Arquitetura, Implementação e Testes de Microsserviços

Este documento contém o conteúdo pronto para cadastro direto das tarefas no Jira da equipe, estruturado com **Título**, **Responsável**, **Status**, **Descrição Detalhada** e **Critérios de Aceitação (DoD)**.

---

### Tarefa 1: `SH-101` — Definição da Arquitetura e Diagramas de Microsserviços
- **Tipo:** Tarefa (*Task*)
- **Responsável:** Integrante 1 / Arquiteto
- **Status Inicial:** Done (Concluído)
- **Descrição:**
  Projetar a arquitetura completa do sistema decompondo o monólito em microsserviços por responsabilidade de negócio. Elaborar diagrama em Mermaid e justificar a separação dos serviços, comunicação REST e isolamento dos bancos de dados (*Database-per-Service*).
- **Critérios de Aceitação (DoD):**
  - [x] Diagrama completo representando todos os microsserviços e destacando visualmente os 3 implementados na Sprint.
  - [x] Tabela de microsserviços com responsabilidades, dados e dependências.
  - [x] Justificativa arquitetural respondendo aos critérios de negócio, benefícios e mitigações.
  - [x] Documento versionado no repositório em `docs/arquitetura.md`.

---

### Tarefa 2: `SH-102` — Estruturação do Repositório e Módulos Independentes
- **Tipo:** Tarefa Técnica (*Tech Task*)
- **Responsável:** Toda a equipe
- **Status Inicial:** Done (Concluído)
- **Descrição:**
  Criar a estrutura do repositório contendo as pastas independentes para cada microsserviço (`services/user-service`, `services/subscription-service`, `services/notification-service`), scripts de automação de build e inclusão de dependências essenciais em `lib/`.
- **Critérios de Aceitação (DoD):**
  - [x] Diretórios de cada microsserviço com separação `src/main/java` e `src/test/java`.
  - [x] Script `scripts/build-all.bat` compilando todos os serviços com sucesso.
  - [x] Repositório preparado para permitir desenvolvimento paralelo via branches individuais.

---

### Tarefa 3: `SH-103` — Implementação do User Service (MS-1 / Porta 8081)
- **Tipo:** História / Tarefa (*Story*)
- **Responsável:** Integrante 1
- **Status:** Done / Em Revisão
- **Descrição:**
  Implementar o microsserviço independente de usuários com endpoints REST para autenticação (login), cadastro e consulta de perfil por ID na porta 8081, com persistência isolada em `data/users.json`.
- **Critérios de Aceitação (DoD):**
  - [x] Endpoints `GET /api/users/health`, `GET /api/users`, `GET /api/users/{id}`, `POST /api/users` e `POST /api/users/login` implementados.
  - [x] Validação de e-mail duplicado (HTTP 409) e campos obrigatórios (HTTP 400).
  - [x] Retorno adequado de códigos HTTP de sucesso (200, 201) e erro (400, 401, 404, 409).
  - [x] Persistência autônoma sem dependência de banco de outros serviços.

---

### Tarefa 4: `SH-104` — Implementação do Subscription Service (MS-2 / Porta 8082)
- **Tipo:** História / Tarefa (*Story*)
- **Responsável:** Integrante 2
- **Status:** Done / Em Revisão
- **Descrição:**
  Implementar o microsserviço de gestão de assinaturas recorrentes com CRUD completo, cálculo de prazos de periodicidade e cancelamento de assinaturas na porta 8082 com persistência em `data/subscriptions.json`.
- **Critérios de Aceitação (DoD):**
  - [x] Endpoints `GET /api/subscriptions`, `GET /api/subscriptions/user/{userId}`, `POST /api/subscriptions` e `PUT /api/subscriptions/{id}/cancel`.
  - [x] Regras de negócio: rejeitar assinaturas com valor menor ou igual a zero (HTTP 400).
  - [x] Transição de status para `CANCELADA` preservando histórico.
  - [x] Base de dados isolada de assinaturas.

---

### Tarefa 5: `SH-105` — Comunicação REST Inter-serviços (Subscription -> User)
- **Tipo:** História / Integração (*Story*)
- **Responsável:** Integrante 2 (em parceria com Integrante 1)
- **Status:** Done / Em Revisão
- **Descrição:**
  Implementar a comunicação síncrona REST obrigatória da seção 3.2 do edital: antes de persistir uma assinatura, o `Subscription Service` consulta via HTTP GET o `User Service` (`http://localhost:8081/api/users/{usuarioId}`).
- **Critérios de Aceitação (DoD):**
  - [x] Cliente HTTP com timeout implementado (`UserClient`).
  - [x] Validação de integridade: se o usuário não existir no User Service, a criação é rejeitada com HTTP 422 Unprocessable Entity.
  - [x] Resiliência: se o User Service estiver fora do ar, responde HTTP 503 Service Unavailable sem corromper a base local.

---

### Tarefa 6: `SH-106` — Implementação do Notification Service (MS-3 / Porta 8083)
- **Tipo:** História / Tarefa (*Story*)
- **Responsável:** Integrante 3
- **Status:** Done / Em Revisão
- **Descrição:**
  Implementar o microsserviço de notificações e alertas de cobrança, consumindo assinaturas ativas do `Subscription Service` via REST para calcular vencimentos iminentes na porta 8083 com persistência em `data/notifications.json`.
- **Critérios de Aceitação (DoD):**
  - [x] Endpoint `POST /api/notifications/check` com antecedência configurável em dias.
  - [x] Consulta síncrona REST ao `Subscription Service` (`GET /api/subscriptions/user/{id}`).
  - [x] Geração e persistência de alertas não lidos com formatação amigável.
  - [x] Endpoints de consulta por usuário e marcação de notificação como lida (`PUT .../read`).

---

### Tarefa 7: `SH-107` — Testes Unitários Automatizados do User Service
- **Tipo:** Testes / QA
- **Responsável:** Integrante 1
- **Status:** Done (Concluído)
- **Descrição:**
  Desenvolver 4 testes unitários isolados com JUnit 5 cobrindo as principais regras de negócio de cadastro e autenticação de usuários sem depender de recursos externos.
- **Critérios de Aceitação (DoD):**
  - [x] `UT-US-01`: Cadastro de usuário com dados válidos (Sucesso).
  - [x] `UT-US-02`: Rejeição de cadastro com e-mail duplicado (Validação/Erro).
  - [x] `UT-US-03`: Autenticação com credenciais válidas (Sucesso).
  - [x] `UT-US-04`: Falha de autenticação com senha incorreta (Erro).
  - [x] 100% dos testes passando na suíte automatizada.

---

### Tarefa 8: `SH-108` — Testes Unitários Automatizados do Subscription Service
- **Tipo:** Testes / QA
- **Responsável:** Integrante 2
- **Status:** Done (Concluído)
- **Descrição:**
  Desenvolver 4 testes unitários isolados com JUnit 5 + Mockito para o serviço de assinaturas, simulando a resposta do UserClient externo.
- **Critérios de Aceitação (DoD):**
  - [x] `UT-SS-01`: Cadastro com dados válidos e mock de usuário existente (Sucesso).
  - [x] `UT-SS-02`: Rejeição de cadastro com valor <= 0 (Validação).
  - [x] `UT-SS-03`: Rejeição de cadastro quando usuário não existe no User Service (Mock 404).
  - [x] `UT-SS-04`: Cancelamento de assinatura ativa para CANCELADA (Sucesso).
  - [x] 100% dos testes passando na suíte automatizada.

---

### Tarefa 9: `SH-109` — Testes Unitários Automatizados do Notification Service
- **Tipo:** Testes / QA
- **Responsável:** Integrante 3
- **Status:** Done (Concluído)
- **Descrição:**
  Desenvolver 4 testes unitários isolados com JUnit 5 + Mockito para as regras de cálculo temporal de alertas e resiliência de rede.
- **Critérios de Aceitação (DoD):**
  - [x] `UT-NS-01`: Geração de alerta para assinatura vencendo dentro do intervalo (Sucesso).
  - [x] `UT-NS-02`: Retorno de lista vazia quando nenhuma assinatura está vencendo (Sucesso).
  - [x] `UT-NS-03`: Rejeição de parâmetro de antecedência negativo (Validação).
  - [x] `UT-NS-04`: Tratamento de erro quando o Subscription Service está indisponível (Resiliência).
  - [x] 100% dos testes passando na suíte automatizada.

---

### Tarefa 10: `SH-110` — Validação das APIs REST e Evidências de Integração
- **Tipo:** Testes / QA
- **Responsável:** Toda a equipe
- **Status:** Done (Concluído)
- **Descrição:**
  Executar a bateria de testes integrados utilizando curl e Postman nos 3 microsserviços rodando simultaneamente em portas separadas e registrar as evidências de resposta.
- **Critérios de Aceitação (DoD):**
  - [x] Evidência de consulta com sucesso.
  - [x] Evidência de cadastro com sucesso.
  - [x] Evidência de comunicação síncrona REST entre serviços (MS-2 -> MS-1 e MS-3 -> MS-2).
  - [x] Evidência de requisições inválidas e códigos HTTP adequados (400, 401, 404, 422, 503).
  - [x] Script `scripts/test-apis.bat` e Coleção Postman versionada em `docs/`.

---

### Tarefa 11: `SH-111` — Documentação Técnica, Contratos de API e README Final
- **Tipo:** Documentação
- **Responsável:** Toda a equipe
- **Status:** Done (Concluído)
- **Descrição:**
  Consolidar todos os artefatos de documentação exigidos no edital: diagrama arquitetural, catálogo de APIs REST, documentação dos testes unitários e guia de execução no README.md.
- **Critérios de Aceitação (DoD):**
  - [x] README.md com instruções passo a passo para o professor executar com 1 clique.
  - [x] docs/apis.md com catálogo completo de endpoints.
  - [x] docs/evidencias_testes.md com logs de execução.
  - [x] docs/guia_equipe_git_jira.md orientando a equipe.

