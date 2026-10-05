# Implementation Plan: Disponibilidade dos Profissionais

**Branch**: `004-disponibilidade-profissionais` | **Date**: 2026-09-29 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/004-disponibilidade-profissionais/spec.md`

## Summary

Implementar o cadastro, consulta, alteração e remoção da disponibilidade semanal dos profissionais da barbearia. Cada período de disponibilidade é vinculado a um profissional existente e composto por dia da semana, horário de início e horário de término. A solução seguirá a arquitetura em camadas definida na Constitution (Controller, Service, Repository, Domain/Model), utilizará DTOs para os contratos de API e persistirá os dados em PostgreSQL via Spring Data JPA com migrations Flyway. A regra de negócio principal reside na camada de Service, que validará horários, conflitos de sobreposição e existência do profissional.

## Technical Context

| Aspecto | Valor |
|---------|-------|
| **Language/Version** | Java 21 (LTS) |
| **Primary Dependencies** | Spring Boot 4.x, Spring WebMvc, Spring Data JPA, Spring Validation (Jakarta Bean Validation), PostgreSQL Driver, Flyway, Lombok, MapStruct |
| **Storage** | PostgreSQL (banco principal); H2 em memória apenas para testes automatizados |
| **Testing** | JUnit 5, Mockito, `@SpringBootTest` para testes de integração |
| **Target Platform** | Linux server executado via Docker Compose |
| **Project Type** | web-service REST API |
| **Performance Goals** | Padrão para API de gestão interna: respostas abaixo de 500 ms em cenários típicos |
| **Constraints** | Respeitar a stack tecnológica fixada na Constitution; não adicionar dependências sem justificativa |
| **Scale/Scope** | Barbearia de pequeno/médio porte; disponibilidade semanal por profissional, com poucos períodos por dia |

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Princípio | Status | Justificativa |
|-----------|--------|---------------|
| I. Arquitetura em Camadas | ✅ Pass | A feature será organizada em Controller, Service, Repository e Domain/Model, com DTOs na fronteira da API. |
| II. Stack Tecnológica Padronizada | ✅ Pass | Serão utilizados Java 21, Spring Boot, Spring WebMvc, Spring Data JPA, Spring Validation, PostgreSQL, Flyway, Lombok e MapStruct, todos aprovados na Constitution. |
| III. Qualidade e Testes | ✅ Pass | Serão entregues testes unitários (JUnit 5 + Mockito) e testes de integração (`@SpringBootTest`). |
| IV. Banco de Dados e Migrations | ✅ Pass | O schema será alterado exclusivamente via migration Flyway, reversível por `down`. |
| V. Mensageria e Cache | ✅ Pass | Não há necessidade de RabbitMQ ou cache para esta feature; o escopo é puramente CRUD síncrono. |
| VI. Observabilidade e Monitoramento | ✅ Pass | Serão mantidos os logs estruturados existentes; não há alteração no padrão de observabilidade. |
| VII. Containerização e Deploy | ✅ Pass | Nenhuma mudança no Dockerfile ou Docker Compose é necessária além da nova migration e código da aplicação. |
| VIII. Comunicação e Notificações | ✅ Pass | Não há envio de e-mail ou eventos assíncronos nesta feature. |

**Parecer final**: Todos os princípios da Constitution são respeitados. Não há violações a justificar.

## Project Structure

### Documentation (this feature)

```text
specs/004-disponibilidade-profissionais/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
src/main/java/com/augustoomb/api_barbearia_do_ze/
├── domain/
│   └── availability/
│       ├── AvailabilityEntity.java
│       ├── AvailabilityRepository.java
│       ├── AvailabilityNotFoundException.java
│       ├── InvalidAvailabilityException.java
│       └── OverlappingAvailabilityException.java
├── application/
│   └── availability/
│       ├── AvailabilityService.java
│       ├── AvailabilityMapper.java
│       ├── CreateAvailabilityRequest.java
│       ├── UpdateAvailabilityRequest.java
│       └── AvailabilityResponse.java
└── infrastructure/
    └── web/
        ├── AvailabilityController.java
        └── GlobalExceptionHandler.java

src/main/resources/
├── db/migration/
│   └── V3__create_availability_table.sql
└── application.yml

src/test/java/com/augustoomb/api_barbearia_do_ze/
├── domain/availability/AvailabilityEntityTest.java
├── application/availability/AvailabilityServiceTest.java
└── infrastructure/web/AvailabilityControllerIntegrationTest.java
```

**Structure Decision**: Foi mantida a estrutura monolítica já existente no projeto, organizada por feature/domain (`availability`). A separação em `domain`, `application` e `infrastructure` preserva a independência da camada de domínio em relação aos frameworks web e de persistência, conforme os padrões já estabelecidos pelas features anteriores (`professional`, `service`). Os DTOs e o mapper ficam na camada `application`, enquanto as entidades e regras de negócio ficam em `domain`.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

Nenhuma violação identificada.
