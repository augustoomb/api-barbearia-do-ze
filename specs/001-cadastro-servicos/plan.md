# Implementation Plan: Cadastro de Serviços

**Branch**: `001-cadastro-serviços` | **Date**: 2026-09-08 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-cadastro-servicos/spec.md`

## Summary

Implementar o cadastro e gestão de serviços oferecidos pela barbearia, permitindo criar, consultar, atualizar parcialmente e ativar/desativar serviços. A solução seguirá a arquitetura em camadas definida na Constitution (Controller, Service, Repository, Domain/Model), utilizará DTOs com validação explícita, Spring Data JPA com PostgreSQL e Flyway para persistência, e será coberta por testes unitários e de integração.

## Technical Context

**Language/Version**: Java 21 (LTS)

**Primary Dependencies**: Spring Boot 4.x, Spring WebMvc, Spring Data JPA, Spring Validation (Bean Validation/Jakarta Validation), Lombok, MapStruct

**Storage**: PostgreSQL (principal), H2 (testes automatizados)

**Testing**: JUnit 5, Mockito, `@SpringBootTest` para testes de integração

**Target Platform**: Servidor Linux via Docker Compose

**Project Type**: web-service (API REST)

**Performance Goals**: Consulta de lista com até 100 serviços em menos de 2 segundos (conforme SC-003 do spec)

**Constraints**: Respeitar stack padronizada da Constitution; todas as alterações de schema via Flyway; sem código sem testes

**Scale/Scope**: Ambiente de barbearia com dezenas de serviços; não há necessidade de paginação nesta feature

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Princípio | Status | Observação |
|-----------|--------|------------|
| I. Arquitetura em Camadas | ✅ Pass | Controller, Service, Repository e Domain/Model serão utilizados |
| II. Stack Tecnológica Padronizada | ✅ Pass | Java 21, Spring Boot, Spring WebMvc, Spring Data JPA, Spring Validation, PostgreSQL, Flyway, H2 (testes), Lombok |
| III. Qualidade e Testes | ✅ Pass | Testes unitários (JUnit 5 + Mockito) e integração (`@SpringBootTest`) serão criados |
| IV. Banco de Dados e Migrations | ✅ Pass | Schema via Flyway; migration reversível |
| V. Mensageria e Cache | ✅ Pass | Não aplicável a esta feature (sem eventos assíncronos) |
| VI. Observabilidade e Monitoramento | ✅ Pass | Logs estruturados e endpoints Actuator herdados do projeto |
| VII. Containerização e Deploy | ✅ Pass | Ambiente via Docker Compose já existente |
| VIII. Comunicação e Notificações | ✅ Pass | Não aplicável a esta feature |

**Resultado**: Todos os princípios da Constitution são respeitados. Nenhuma violação justificada.

## Project Structure

### Documentation (this feature)

```text
specs/001-cadastro-servicos/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
src/main/java/com/barbeariadose/api/
├── domain/
│   └── service/
│       ├── ServiceEntity.java
│       └── ServiceRepository.java
├── application/
│   └── service/
│       ├── ServiceService.java
│       ├── CreateServiceRequest.java
│       ├── UpdateServiceRequest.java
│       ├── ServiceResponse.java
│       └── ServiceMapper.java
├── infrastructure/
│   └── web/
│       ├── ServiceController.java
│       └── GlobalExceptionHandler.java
└── config/
    └── ...

src/test/java/com/barbeariadose/api/
├── domain/service/
│   └── ServiceEntityTest.java
├── application/service/
│   └── ServiceServiceTest.java
└── infrastructure/web/
    └── ServiceControllerIntegrationTest.java

src/main/resources/
├── db/migration/
│   └── V1__create_services_table.sql
└── application.yml
```

**Structure Decision**: Estrutura monolítica single-project, com separação em camadas conforme Constitution. A camada `domain` contém entidade e repositório (interface), `application` contém serviço de aplicação e DTOs, e `infrastructure` contém controller REST e tratamento global de exceções. Testes unitários cobrem domínio e aplicação; testes de integração cobrem controller.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

Nenhuma violação identificada.
