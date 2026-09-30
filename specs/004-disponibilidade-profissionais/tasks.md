# Tasks: Disponibilidade dos Profissionais

**Input**: Design documents from `/specs/004-disponibilidade-profissionais/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Incluídos, conforme exigido pela Constitution (Princípio III — Qualidade e Testes).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Estrutura base da feature no projeto existente.

- [ ] T001 [P] Criar pacote `com.augustoomb.api_barbearia_do_ze.domain.availability` em `src/main/java/`
- [ ] T002 [P] Criar pacote `com.augustoomb.api_barbearia_do_ze.application.availability` em `src/main/java/`
- [ ] T003 [P] Criar pacote de testes `com.augustoomb.api_barbearia_do_ze.domain.availability` em `src/test/java/`
- [ ] T004 [P] Criar pacote de testes `com.augustoomb.api_barbearia_do_ze.application.availability` em `src/test/java/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestrutura compartilhada que DEVE estar pronta antes de qualquer user story.

**⚠️ CRITICAL**: Nenhuma user story pode começar até esta fase estar completa.

- [ ] T005 Criar migration `V3__create_availability_table.sql` em `src/main/resources/db/migration/` com: (a) criação da tabela `availability` incluindo FK para `professionals`, constraints de dia da semana (`1`-`7`) e de horário (`end_time > start_time`), índice `idx_availability_professional_day`; (b) instrução de rollback `DROP TABLE availability;`.
- [ ] T006 [P] Criar `AvailabilityEntity.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/` com campos `id`, `professional`, `dayOfWeek`, `startTime`, `endTime`, `createdAt`, `updatedAt`.
- [ ] T007 [P] Criar `AvailabilityRepository.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/` com método para buscar períodos por profissional e dia da semana.
- [ ] T008 [P] Criar `AvailabilityNotFoundException.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/`.
- [ ] T009 [P] Criar `InvalidAvailabilityException.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/`.
- [ ] T010 [P] Criar `OverlappingAvailabilityException.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/`.
- [ ] T011 [P] Criar `CreateAvailabilityRequest.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/` com validações de `dayOfWeek`, `startTime` e `endTime`.
- [ ] T012 [P] Criar `UpdateAvailabilityRequest.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/` com os mesmos campos do request de criação.
- [ ] T013 [P] Criar `AvailabilityResponse.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/` com os campos de resposta.
- [ ] T014 Criar `AvailabilityMapper.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/` com conversões entre entity, request e response (depende de T006, T011, T012, T013).

**Checkpoint**: Foundational pronta — migrations, entidade, repositório, exceções e DTOs disponíveis para as user stories.

---

## Phase 3: User Story 1 — Cadastrar disponibilidade de um profissional (Priority: P1) 🎯 MVP

**Goal**: Permitir o cadastro de períodos de disponibilidade vinculados a um profissional existente, com validações de horário e sobreposição.

**Independent Test**: Cadastrar um ou mais períodos via `POST /api/v1/professionals/{professionalId}/availability` e verificar que são persistidos corretamente.

### Tests for User Story 1

- [ ] T015 [P] [US1] Criar teste unitário `AvailabilityEntityTest.java` em `src/test/java/com/augustoomb/api_barbearia_do_ze/domain/availability/` validando criação da entidade.
- [ ] T016 [P] [US1] Criar teste unitário `AvailabilityServiceTest.java` em `src/test/java/com/augustoomb/api_barbearia_do_ze/application/availability/` para criação com profissional inexistente, horário inválido e sobreposição.

### Implementation for User Story 1

- [ ] T017 [US1] Implementar método `create(UUID professionalId, CreateAvailabilityRequest request)` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/AvailabilityService.java` (valida profissional, horário e sobreposição).
- [ ] T018 [US1] Implementar endpoint `POST /api/v1/professionals/{professionalId}/availability` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/AvailabilityController.java`.

**Checkpoint**: User Story 1 funcional e testável independentemente.

---

## Phase 4: User Story 2 — Consultar disponibilidade de um profissional (Priority: P2)

**Goal**: Permitir a consulta dos períodos de disponibilidade cadastrados de um profissional.

**Independent Test**: Consultar disponibilidades via `GET /api/v1/professionals/{professionalId}/availability` e verificar a lista completa ou vazia.

### Tests for User Story 2

- [ ] T019 [P] [US2] Adicionar testes unitários em `AvailabilityServiceTest.java` para listagem de disponibilidades por profissional.
- [ ] T020 [P] [US2] Criar teste de integração `AvailabilityControllerIntegrationTest.java` em `src/test/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/` para o endpoint de listagem.

### Implementation for User Story 2

- [ ] T021 [US2] Implementar método `findByProfessional(UUID professionalId)` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/AvailabilityService.java`, retornando períodos ordenados por dia da semana e horário de início crescentes.
- [ ] T022 [US2] Implementar endpoint `GET /api/v1/professionals/{professionalId}/availability` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/AvailabilityController.java`.

**Checkpoint**: User Stories 1 e 2 funcionam independentemente.

---

## Phase 5: User Story 3 — Alterar e remover disponibilidade de um profissional (Priority: P2)

**Goal**: Permitir a alteração e remoção de períodos de disponibilidade, mantendo o profissional vinculado inalterado e retornando erro para recursos inexistentes.

**Independent Test**: Alterar e remover períodos via `PUT` e `DELETE`, respectivamente, e verificar as regras de validação e erro.

### Tests for User Story 3

- [ ] T023 [P] [US3] Adicionar testes unitários em `AvailabilityServiceTest.java` para alteração com profissional inexistente, disponibilidade inexistente, horário inválido, sobreposição e tentativa de mudar profissional.
- [ ] T024 [P] [US3] Adicionar testes unitários em `AvailabilityServiceTest.java` para remoção de disponibilidade existente e inexistente.
- [ ] T025 [P] [US3] Adicionar testes de integração em `AvailabilityControllerIntegrationTest.java` para os endpoints de alteração e remoção.

### Implementation for User Story 3

- [ ] T026 [US3] Implementar método `update(UUID professionalId, UUID availabilityId, UpdateAvailabilityRequest request)` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/AvailabilityService.java`.
- [ ] T027 [US3] Implementar método `delete(UUID professionalId, UUID availabilityId)` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/availability/AvailabilityService.java`.
- [ ] T028 [US3] Implementar endpoint `PUT /api/v1/professionals/{professionalId}/availability/{availabilityId}` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/AvailabilityController.java`.
- [ ] T029 [US3] Implementar endpoint `DELETE /api/v1/professionals/{professionalId}/availability/{availabilityId}` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/AvailabilityController.java`.

**Checkpoint**: Todas as user stories funcionam independentemente.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Ajustes que afetam múltiplas user stories e alinhamento com a Constitution.

- [ ] T030 [P] Atualizar `GlobalExceptionHandler.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/` para traduzir `AvailabilityNotFoundException`, `InvalidAvailabilityException` e `OverlappingAvailabilityException` em respostas HTTP padronizadas.
- [ ] T031 [P] Adicionar anotações OpenAPI/SpringDoc no `AvailabilityController.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/`.
- [ ] T032 [P] Adicionar testes de integração restantes em `AvailabilityControllerIntegrationTest.java` cobrindo cenários de borda (período consecutivo, sobreposição, horário inválido, profissional inexistente).
- [ ] T033 Executar `quickstart.md` validation manualmente com curls/HTTP client e verificar todos os cenários.
- [ ] T034 Executar `./mvnw test` e garantir que todos os testes unitários e de integração da feature passam.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode iniciar imediatamente.
- **Foundational (Phase 2)**: Depende do Setup. BLOQUEIA todas as user stories.
- **User Stories (Phase 3+)**: Dependem da Foundational phase.
  - As user stories podem ser executadas em paralelo se houver capacidade.
  - Ou sequencialmente em ordem de prioridade: P1 → P2 → P2.
- **Polish (Final Phase)**: Depende da conclusão das user stories desejadas.

### User Story Dependencies

- **User Story 1 (P1)**: Pode iniciar após Foundational. Não depende de outras stories.
- **User Story 2 (P2)**: Pode iniciar após Foundational. Depende indiretamente da US1 apenas para existência de dados em cenários manuais, mas é testável de forma independente.
- **User Story 3 (P2)**: Pode iniciar após Foundational. Depende da existência de períodos (US1) para testes de alteração/remoção, mas pode ser testada com setup próprio.

### Within Each User Story

- Models/Foundations antes de services.
- Services antes de controllers/endpoints.
- Implementação core antes de testes de integração.
- Story completa antes de avançar para a próxima prioridade.

### Parallel Opportunities

- Todas as tarefas do Setup (Phase 1) marcadas [P] podem rodar em paralelo.
- Todas as tarefas da Foundational (Phase 2) marcadas [P] podem rodar em paralelo, exceto T014 (mapper), que depende das entities/DTOs.
- Após a Foundational, as três user stories podem ser trabalhadas em paralelo por desenvolvedores diferentes.
- Testes unitários e de integração dentro de uma mesma story podem ser escritos em paralelo.

---

## Parallel Example: User Story 1

```bash
# Models + Repository + Exceptions (parallel):
Task: "Create AvailabilityEntity.java in src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/"
Task: "Create AvailabilityRepository.java in src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/"
Task: "Create AvailabilityNotFoundException.java in src/main/java/com/augustoomb/api_barbearia_do_ze/domain/availability/"

# Tests (parallel):
Task: "Create AvailabilityEntityTest.java in src/test/java/com/augustoomb/api_barbearia_do_ze/domain/availability/"
Task: "Create AvailabilityServiceTest.java in src/test/java/com/augustoomb/api_barbearia_do_ze/application/availability/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup.
2. Complete Phase 2: Foundational (CRÍTICO — bloqueia todas as stories).
3. Complete Phase 3: User Story 1.
4. **STOP and VALIDATE**: Testar a US1 independentemente via `./mvnw test` e quickstart.
5. Deploy/demo se estiver pronto.

### Incremental Delivery

1. Setup + Foundational → Base pronta.
2. User Story 1 → Testar independentemente → Deploy/Demo (MVP!).
3. User Story 2 → Testar independentemente → Deploy/Demo.
4. User Story 3 → Testar independentemente → Deploy/Demo.
5. Polish → Validar cross-cutting concerns.
6. Cada story agrega valor sem quebrar as anteriores.

### Parallel Team Strategy

Com múltiplos desenvolvedores:

1. Time completa Setup + Foundational juntos.
2. Após Foundational:
   - Desenvolvedor A: User Story 1
   - Desenvolvedor B: User Story 2
   - Desenvolvedor C: User Story 3
3. Cada story é finalizada e testada de forma independente.
4. Fase Polish realizada em conjunto.

---

## Notes

- `[P]` tasks = arquivos diferentes, sem dependências.
- `[Story]` label mapeia a tarefa para a user story específica.
- Cada user story deve ser implementável e testável de forma independente.
- Verificar falha dos testes antes da implementação (TDD).
- Commit após cada tarefa ou grupo lógico.
- Parar em qualquer checkpoint para validar a story independentemente.
- Evitar: tarefas vagas, conflitos no mesmo arquivo e dependências cross-story que quebrem a independência.
