---

description: "Task list for implementing the Cadastro de Serviços feature"
---

# Tasks: Cadastro de Serviços

**Input**: Design documents from `/specs/001-cadastro-servicos/`

**Prerequisites**: plan.md, spec.md, data-model.md, contracts/api.md, quickstart.md

**Tests**: Tasks include unit and integration tests as required by the project Constitution.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)

## Path Conventions

- Base package: `com.barbeariadose.api`
- Source root: `src/main/java/com/barbeariadose/api/`
- Test root: `src/test/java/com/barbeariadose/api/`
- Migrations: `src/main/resources/db/migration/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify and prepare the project structure and dependencies.

- [ ] T001 Verify project structure exists at `src/main/java/com/barbeariadose/api/` and create missing directories: `domain/service/`, `application/service/`, `infrastructure/web/`, and corresponding test directories
- [ ] T002 Verify `pom.xml` includes required dependencies: Spring Web, Spring Data JPA, Spring Validation, Flyway, PostgreSQL driver, H2, Lombok, and MapStruct

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain, persistence, DTOs, and cross-cutting infrastructure that MUST be complete before user stories.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T003 [P] Create Flyway migration `src/main/resources/db/migration/V1__create_services_table.sql` with `services` table matching data-model.md, including rollback instruction `DROP TABLE IF EXISTS services;`
- [ ] T005 [P] Create `src/main/java/com/barbeariadose/api/domain/service/ServiceEntity.java` JPA entity with all fields and lifecycle timestamps, using `boolean active` field
- [ ] T006 Create `src/main/java/com/barbeariadose/api/domain/service/ServiceRepository.java` Spring Data JPA interface with `findAllByActiveTrue()` query method to support future scheduling features (depends on T005)
- [ ] T007 [P] Create `src/main/java/com/barbeariadose/api/domain/service/NameNormalizer.java` utility to normalize service names (lowercase, remove accents, trim)
- [ ] T008 [P] Create domain exceptions in `src/main/java/com/barbeariadose/api/domain/service/`: `ServiceNotFoundException`, `DuplicateServiceNameException`, and `InvalidServiceException`
- [ ] T009 [P] Create request/response DTOs in `src/main/java/com/barbeariadose/api/application/service/`: `CreateServiceRequest.java`, `UpdateServiceRequest.java`, and `ServiceResponse.java`
- [ ] T010 Create `src/main/java/com/barbeariadose/api/application/service/ServiceMapper.java` to convert between Entity, Request, and Response DTOs (depends on T005 and T009)
- [ ] T011 [P] Create `src/main/java/com/barbeariadose/api/infrastructure/web/ApiResponse.java` standardized response envelope
- [ ] T012 [P] Create `src/main/java/com/barbeariadose/api/infrastructure/web/GlobalExceptionHandler.java` to handle validation, not found, conflict, and generic errors with standardized responses

**Checkpoint**: Foundation ready — user story implementation can now begin in parallel.

---

## Phase 3: User Story 1 - Cadastrar novo serviço (Priority: P1) 🎯 MVP

**Goal**: Allow managers to create new barbershop services with validation and active-by-default status.

**Independent Test**: Send a POST request to `/api/v1/services` and verify the service is persisted with `active: true` and can be retrieved.

### Tests for User Story 1

- [ ] T013 [P] [US1] Write unit tests for `ServiceService.create()` in `src/test/java/com/barbeariadose/api/application/service/ServiceServiceTest.java` covering valid creation, blank name, duplicate normalized name, zero/negative duration, and negative price
- [ ] T014 [P] [US1] Write integration tests for `POST /api/v1/services` in `src/test/java/com/barbeariadose/api/infrastructure/web/ServiceControllerIntegrationTest.java` covering 201, 400, and 409 responses

### Implementation for User Story 1

- [ ] T015 [US1] Implement `ServiceService.create()` in `src/main/java/com/barbeariadose/api/application/service/ServiceService.java` with all business validations
- [ ] T016 [US1] Implement `POST /api/v1/services` endpoint in `src/main/java/com/barbeariadose/api/infrastructure/web/ServiceController.java`

**Checkpoint**: User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - Consultar serviços cadastrados (Priority: P1)

**Goal**: Allow managers to list all registered services, active or inactive.

**Independent Test**: After creating services via POST, send GET `/api/v1/services` and receive all persisted services.

### Tests for User Story 2

- [ ] T017 [P] [US2] Write unit tests for `ServiceService.findAll()` in `ServiceServiceTest.java`
- [ ] T018 [P] [US2] Write integration tests for `GET /api/v1/services` in `ServiceControllerIntegrationTest.java` covering populated and empty lists

### Implementation for User Story 2

- [ ] T019 [US2] Implement `ServiceService.findAll()` in `ServiceService.java`
- [ ] T020 [US2] Implement `GET /api/v1/services` endpoint in `ServiceController.java`

**Checkpoint**: User Stories 1 and 2 should both work independently.

---

## Phase 5: User Story 3 - Consultar serviço específico (Priority: P2)

**Goal**: Allow managers to retrieve a single service by its identifier.

**Independent Test**: Send GET `/api/v1/services/{id}` and receive 200 for existing service or 404 for missing service.

### Tests for User Story 3

- [ ] T021 [P] [US3] Write unit tests for `ServiceService.findById()` in `ServiceServiceTest.java`
- [ ] T022 [P] [US3] Write integration tests for `GET /api/v1/services/{id}` in `ServiceControllerIntegrationTest.java`

### Implementation for User Story 3

- [ ] T023 [US3] Implement `ServiceService.findById()` in `ServiceService.java`
- [ ] T024 [US3] Implement `GET /api/v1/services/{id}` endpoint in `ServiceController.java`

**Checkpoint**: User Stories 1, 2, and 3 should all work independently.

---

## Phase 6: User Story 4 - Atualizar serviço (Priority: P2)

**Goal**: Allow managers to partially update service data without changing status.

**Independent Test**: Send PATCH `/api/v1/services/{id}` with only `price` and verify the service is updated while other fields remain unchanged.

### Tests for User Story 4

- [ ] T025 [P] [US4] Write unit tests for `ServiceService.update()` in `ServiceServiceTest.java` covering partial updates, blank request, duplicate name, invalid duration, and invalid price
- [ ] T026 [P] [US4] Write integration tests for `PATCH /api/v1/services/{id}` in `ServiceControllerIntegrationTest.java`

### Implementation for User Story 4

- [ ] T027 [US4] Implement `ServiceService.update()` in `ServiceService.java` with partial update logic and validation of provided fields only
- [ ] T028 [US4] Implement `PATCH /api/v1/services/{id}` endpoint in `ServiceController.java`

**Checkpoint**: User Stories 1–4 should all work independently.

---

## Phase 7: User Story 5 - Ativar ou desativar serviço (Priority: P2)

**Goal**: Allow managers to activate or deactivate a service through dedicated endpoints, with idempotency and name-conflict checks on activation.

**Independent Test**: Send POST `/api/v1/services/{id}/deactivate` and verify `active: false`; send POST `/api/v1/services/{id}/activate` twice and verify both return success.

### Tests for User Story 5

- [ ] T029 [P] [US5] Write unit tests for `ServiceService.activate()` and `ServiceService.deactivate()` in `ServiceServiceTest.java` covering status transitions, idempotency, and activation name conflict
- [ ] T030 [P] [US5] Write integration tests for activation/deactivation endpoints in `ServiceControllerIntegrationTest.java`

### Implementation for User Story 5

- [ ] T031 [US5] Implement `ServiceService.activate()` and `ServiceService.deactivate()` in `ServiceService.java` with idempotency and conflict validation
- [ ] T032 [US5] Implement `POST /api/v1/services/{id}/activate` and `POST /api/v1/services/{id}/deactivate` endpoints in `ServiceController.java`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Quality gates, documentation, and final validation.

- [ ] T033 [P] Run all unit and integration tests with `./mvnw test` and fix any failures
- [ ] T034 [P] Execute the validation scenarios from `quickstart.md` against a running local instance
- [ ] T035 [P] Verify code coverage and add missing tests if needed
- [ ] T035b [P] Validate that `GET /api/v1/services` returns a list of 100 services in under 2 seconds using an integration or performance test
- [ ] T036 Verify OpenAPI/SpringDoc annotations are present on `ServiceController.java` endpoints
- [ ] T037 Review structured logging and ensure `trace-id`/`correlation-id` are propagated in controller and service logs
- [ ] T038 Run `./mvnw verify` or equivalent quality gate to confirm build passes

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Setup completion — blocks all user stories.
- **User Stories (Phase 3–7)**: All depend on Foundational phase completion.
  - Execute in priority order (P1 → P2) or in parallel if team capacity allows.
- **Polish (Phase 8)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: No dependencies on other stories; delivers MVP.
- **User Story 2 (P1)**: Depends only on foundation; reuses ServiceEntity and repository.
- **User Story 3 (P2)**: Depends only on foundation; reuses existing components.
- **User Story 4 (P2)**: Depends only on foundation; reuses validation and DTOs.
- **User Story 5 (P2)**: Depends only on foundation; reuses repository and normalized name logic.

### Within Each User Story

- Unit and integration tests can be written in parallel once foundation is ready.
- Service implementation before controller endpoint.
- Endpoint implementation before integration tests can pass.

### Parallel Opportunities

- Foundational tasks T003, T005, T007, T008, T009, T011, and T012 can run in parallel; T006 depends on T005 and T010 depends on T005 and T009.
- Tests for each user story (T013–T014, T017–T018, etc.) can run in parallel with each other.
- Different user stories can be implemented in parallel by different developers after foundation is complete.
- Polish tasks T033–T038 can run mostly in parallel.

---

## Parallel Example: User Story 1

```bash
# Unit and integration tests can be drafted in parallel:
Task: "Write unit tests for ServiceService.create() in ServiceServiceTest.java"
Task: "Write integration tests for POST /api/v1/services in ServiceControllerIntegrationTest.java"

# Then implement service and controller (sequential dependency):
Task: "Implement ServiceService.create() in ServiceService.java"
Task: "Implement POST /api/v1/services endpoint in ServiceController.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup.
2. Complete Phase 2: Foundational (critical — blocks all stories).
3. Complete Phase 3: User Story 1 (create service).
4. **STOP and VALIDATE**: Test User Story 1 independently using quickstart scenario 1.
5. Deploy/demo if ready.

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready.
2. Add User Story 1 → Test independently → Deploy/Demo (MVP).
3. Add User Story 2 → Test independently → Deploy/Demo.
4. Add User Stories 3, 4, 5 → Test independently → Deploy/Demo.
5. Each story adds value without breaking previous stories.

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together.
2. Once foundation is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
   - Developer D: User Story 4
   - Developer E: User Story 5
3. Stories complete and integrate independently.

---

## Notes

- [P] tasks = different files, no dependencies.
- [Story] label maps task to specific user story for traceability.
- Each user story should be independently completable and testable.
- Verify tests fail before implementing (red-green-refactor recommended but not mandatory).
- Commit after each task or logical group.
- Stop at any checkpoint to validate a story independently.
- Avoid vague tasks, same-file conflicts, and cross-story dependencies that break independence.
