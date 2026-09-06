# Open Finance Consent Hub

> Simulação do ciclo de vida de consentimentos e agregação de dados financeiros  
> conforme o modelo do **Open Finance Brasil** — Java 21, Spring Boot 4, Clean Architecture.

---

## Sobre o Projeto

Este projeto simula o papel de uma **Instituição Receptora (TPP)** no ecossistema do Open Finance Brasil,
regulamentado pelo Banco Central. O sistema gerencia o ciclo de vida completo do **Consentimento**,
desde a criação até a expiração, e agrega dados de contas e cartões de múltiplas instituições transmissoras.

### Por que esse projeto é relevante?

O Open Finance é a infraestrutura central das fintechs e bancos digitais no Brasil.
Entender e implementar o fluxo de consentimentos — com segurança, eventos e auditoria —
é uma habilidade diretamente aplicável em empresas como Nubank, BTG, Itaú e XP.

---

## Funcionalidades

- **Ciclo de vida completo do Consentimento**
    - `AWAITING_AUTHORISATION` → `AUTHORISED` → `REVOKED` | `REJECTED` | `EXPIRED`
    - Expiração automática via job agendado
    - Extensão de prazo para consentimentos autorizados

- **Segurança FAPI-inspired**
    - JWT assinado com RS256 (chaves RSA geradas localmente)
    - OAuth2 Resource Server com validação de scopes por endpoint
    - mTLS simulado com certificados self-signed
    - CPF do usuário extraído diretamente do token JWT

- **Outbox Pattern**
    - Eventos de mudança de status persistidos atomicamente
    - Relay job que processa eventos pendentes
    - Trilha de auditoria imutável em tabela dedicada

- **Agregação Multi-banco**
    - Mock Bank A: contas corrente, poupança e cartão de crédito
    - Mock Bank B: conta corrente
    - Tolerância a falha: falha em um banco não derruba a agregação

---

## Arquitetura

```
adapter/in/web          → Controllers, DTOs, Exception Handler
application/            → Use Cases (orquestração sem lógica de infra)
domain/                 → Entidades, Aggregates, Ports, Domain Events
adapter/out/persistence → JPA Entities, Repositories, Mappers
adapter/out/http        → Clientes HTTP dos Mock Banks
config/                 → Spring Security, RestTemplate
```

**Decisões de design:**
- **Clean Architecture**: dependências apontam sempre para o domínio
- **Aggregate Root**: `Consent` encapsula toda a lógica de estado; nenhum serviço muda status diretamente
- **Domain Events**: toda transição de estado gera um evento imutável (`record`)
- **Outbox Pattern**: garante consistência entre banco e bus de eventos sem two-phase commit
- **ProblemDetail (RFC 9457)**: respostas de erro padronizadas

---

## Stack

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 4 | Framework base |
| Spring Security + OAuth2 | JWT RS256 + validação de scopes |
| Spring Data JPA | Persistência |
| PostgreSQL 16 | Banco de dados |
| Flyway | Migrations versionadas |
| Testcontainers | Testes de integração com banco real |
| Micrometer + Prometheus | Métricas customizadas |
| Docker Compose | Infraestrutura local |

---

## Fluxo de uso (Insomnia)

### 1. Gerar token de acesso
```
POST https://localhost:8080/auth/token
Content-Type: application/x-www-form-urlencoded

clientId=tpp-cliente-001&scope=consents:write consents:read
```

### 2. Criar consentimento
```
POST https://localhost:8080/consents
Authorization: Bearer {token}

{
  "businessEntityCnpj": "12345678000195",
  "permissions": ["ACCOUNTS_READ", "ACCOUNTS_BALANCES_READ", "CREDIT_CARDS_ACCOUNTS_READ"],
  "expirationDateTime": "2026-12-31T23:59:59Z"
}
```

### 3. Autorizar
```
PATCH https://localhost:8080/consents/{id}/authorise
Authorization: Bearer {token}
```

### 4. Agregar dados
```
GET https://localhost:8080/aggregation/consents/{id}/data
Authorization: Bearer {token}
```

---

## Endpoints

| Método | Rota | Scope | Descrição |
|---|---|---|---|
| POST | `/auth/token` | — | Gera JWT de acesso |
| POST | `/consents` | `consents:write` | Cria consentimento |
| GET | `/consents/{id}` | `consents:read` | Consulta consentimento |
| PATCH | `/consents/{id}/authorise` | `consents:write` | Autoriza consentimento |
| PATCH | `/consents/{id}/reject` | `consents:write` | Rejeita consentimento |
| DELETE | `/consents/{id}` | `consents:write` | Revoga consentimento |
| POST | `/consents/{id}/extends` | `consents:write` | Estende expiração |
| GET | `/aggregation/consents/{id}/data` | `consents:read` | Agrega dados dos bancos |
| GET | `/actuator/prometheus` | — | Métricas Prometheus |

---

## Métricas

| Métrica | Descrição |
|---|---|
| `openfinance.consents.created` | Total de consentimentos criados |
| `openfinance.consents.expired` | Total expirados pelo job agendado |

Disponíveis em `https://localhost:8080/actuator/prometheus`

---

## Testes

```bash
./mvnw test
```

- **Unitários:** máquina de estados do `Consent` (8 cenários)
- **Integração:** ciclo de vida completo com Testcontainers + banco PostgreSQL real