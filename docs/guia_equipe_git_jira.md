# 👥 Guia de Trabalho em Equipe — Git & Jira
## Sprint 07 — Divisão em 3 Integrantes

Este guia foi elaborado para que os 3 membros da equipe desenvolvam simultaneamente, sem conflitos no GitHub e mantendo 100% de rastreabilidade com o Jira.

---

## 1. Atribuição dos Microsserviços e Tarefas

| Integrante | Microsserviço Atribuído | Diretório de Trabalho | Tarefas Principais no Jira |
| :--- | :--- | :--- | :--- |
| **Integrante 1** (ex: Yago) | **User Service** (Porta 8081) | `services/user-service/` | `SH-103` (Implementação User Service)<br>`SH-107` (Testes Unitários User) |
| **Integrante 2** | **Subscription Service** (Porta 8082) | `services/subscription-service/` | `SH-104` (Implementação Subscription Service)<br>`SH-105` (Comunicação REST Inter-serviços)<br>`SH-108` (Testes Unitários Subscription) |
| **Integrante 3** | **Notification Service** (Porta 8083) | `services/notification-service/` | `SH-106` (Implementação Notification Service)<br>`SH-109` (Testes Unitários Notification) |
| **Toda a Equipe** | Documentação e Integração | `docs/` e `README.md` | `SH-101`, `SH-102`, `SH-110`, `SH-111` |

---

## 2. Fluxo de Trabalho no Git (Passo a Passo)

### 2.1. Clonar e Atualizar a Branch Principal
Antes de iniciar, certifique-se de estar com a última versão da `main`:
```bash
git checkout main
git pull origin main
```

### 2.2. Criar sua Branch Individual
Cada integrante cria sua branch a partir da `main`:
- **Integrante 1:**
  ```bash
  git checkout -b feature/user-service
  ```
- **Integrante 2:**
  ```bash
  git checkout -b feature/subscription-service
  ```
- **Integrante 3:**
  ```bash
  git checkout -b feature/notification-service
  ```

### 2.3. Padrão de Mensagens de Commit (Rastreabilidade Jira)
Conforme exigido no edital (Seção 7.3), utilize o identificador da tarefa no início de cada commit:
```bash
# Exemplos do Integrante 1:
git commit -m "SH-103: Adiciona endpoint de login no user-service"
git commit -m "SH-107: Implementa testes unitarios de cadastro no user-service"

# Exemplos do Integrante 2:
git commit -m "SH-104: Cria regras de validacao de valor no subscription-service"
git commit -m "SH-105: Implementa UserClient REST para consultar user-service"

# Exemplos do Integrante 3:
git commit -m "SH-106: Implementa verificacao de vencimento de assinaturas"
git commit -m "SH-109: Implementa testes unitarios com mockito no notification-service"
```

### 2.4. Como Testar Localmente Antes de Subir o Código
Antes de dar push, execute o script de validação de testes:
```bash
scripts\run-tests.bat
```
Se todos os testes passarem (`Total: 12, Sucessos: 12`), o código está seguro para ser enviado.

### 2.5. Enviar a Branch e Abrir Pull Request (PR)
```bash
git push -u origin feature/sua-branch
```
No GitHub:
1. Abra um **Pull Request (PR)** da sua branch para a `main`.
2. Adicione no título do PR o código da tarefa (ex: `[SH-104] Implementação do Subscription Service`).
3. Peça a revisão de pelo menos 1 colega.
4. Após aprovação, realize o **Merge** na `main`.

---

## 3. Gestão no Jira

1. Ao iniciar o trabalho, mova sua tarefa de **To Do** para **In Progress**.
2. Cole o link do respectivo Pull Request / commits no comentário da tarefa.
3. Ao finalizar e aprovar o PR, mova a tarefa para **Done**.

