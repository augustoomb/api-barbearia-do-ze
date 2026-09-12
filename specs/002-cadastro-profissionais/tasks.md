# Tasks: Cadastro de Profissionais

**Input**: Design documents from `/specs/002-cadastro-profissionais/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Incluídos conforme Princípio III da Constitution (Qualidade e Testes — NON-NEGOTIABLE).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Estrutura base do projeto já existe; esta fase cobre apenas verificações e ajustes iniciais necessários.

- [X] T001 Verificar e, se necessário, ajustar dependências no `pom.xml` para garantir que Spring Data JPA, Spring Validation, Lombok e MapStruct estão disponíveis e compatíveis com o projeto
- [X] T002 Verificar `GlobalExceptionHandler.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/` e adicionar handlers para `ProfessionalNotFoundException` e `DuplicateProfessionalEmailException` caso ainda não existam

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestrutura central que DEVE estar completa antes de qualquer user story.

**⚠️ CRITICAL**: Nenhum trabalho de user story pode começar até esta fase estar completa.

- [X] T003 Criar migration Flyway `V2__create_professionals_table.sql` em `src/main/resources/db/migration/` com a tabela `professionals` conforme data-model.md
- [X] T004 Criar classe `ProfessionalEntity.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/professional/` com os atributos id, name, email, active, createdAt e updatedAt
- [X] T005 Criar interface `ProfessionalRepository.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/professional/` estendendo JpaRepository
- [X] T006 Criar exceção `ProfessionalNotFoundException.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/professional/`
- [X] T007 Criar exceção `DuplicateProfessionalEmailException.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/professional/`
- [X] T008 Criar exceção `InvalidProfessionalException.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/professional/` para validações genéricas de domínio
- [X] T009 Criar classe `EmailNormalizer.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/domain/professional/` para normalização de e-mail (lowercase + trim)

**Checkpoint**: Foundation ready — a tabela existe, a entidade, o repositório e as exceções de domínio estão criados.

---

## Phase 3: User Story 1 - Cadastrar novo profissional (Priority: P1) 🎯 MVP

**Goal**: Permitir que o gestor cadastre um novo profissional com nome, e-mail e status ativo, respeitando as regras de validação.

**Independent Test**: Criar um profissional via POST `/api/v1/professionals` e verificar que ele é persistido com `active: true`.

### Tests for User Story 1

- [X] T010 [P] [US1] Criar teste unitário `ProfessionalEntityTest.java` em `src/test/java/com/augustoomb/api_barbearia_do_ze/domain/professional/` validando criação e regras básicas da entidade
- [X] T011 [P] [US1] Criar teste de integração `ProfessionalControllerIntegrationTest.java` em `src/test/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/` para os cenários de cadastro com sucesso, duplicidade de e-mail e variação de maiúsculas/minúsculas

### Implementation for User Story 1

- [X] T012 [P] [US1] Criar DTO `CreateProfessionalRequest.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/` com validações de nome e e-mail
- [X] T013 [P] [US1] Criar DTO `ProfessionalResponse.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/`
- [X] T014 [P] [US1] Criar mapper `ProfessionalMapper.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/`
- [X] T015 [US1] Implementar método `createProfessional` em `ProfessionalService.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/` com validação de unicidade de e-mail
- [X] T016 [US1] Implementar endpoint POST `/api/v1/professionals` em `ProfessionalController.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/`

**Checkpoint**: User Story 1 deve estar totalmente funcional e testável independentemente.

---

## Phase 4: User Story 2 - Consultar profissionais cadastrados (Priority: P1)

**Goal**: Permitir que o gestor consulte a lista de todos os profissionais cadastrados.

**Independent Test**: Após cadastrar profissionais, chamar GET `/api/v1/professionals` e verificar que a lista retorna todos os registros.

### Tests for User Story 2

- [X] T017 [P] [US2] Adicionar cenários de teste em `ProfessionalControllerIntegrationTest.java` para listagem com e sem profissionais cadastrados

### Implementation for User Story 2

- [X] T018 [US2] Implementar método `listAllProfessionals` em `ProfessionalService.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/`
- [X] T019 [US2] Implementar endpoint GET `/api/v1/professionals` em `ProfessionalController.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/`

**Checkpoint**: User Stories 1 e 2 devem funcionar independentemente.

---

## Phase 5: User Story 3 - Consultar profissional específico (Priority: P2)

**Goal**: Permitir que o gestor consulte os detalhes de um profissional específico pelo identificador.

**Independent Test**: Após cadastrar um profissional, chamar GET `/api/v1/professionals/{id}` e verificar que os dados completos são retornados; verificar 404 para id inexistente.

### Tests for User Story 3

- [X] T020 [P] [US3] Adicionar cenários de teste em `ProfessionalControllerIntegrationTest.java` para consulta por id existente e inexistente

### Implementation for User Story 3

- [X] T021 [US3] Implementar método `findProfessionalById` em `ProfessionalService.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/`
- [X] T022 [US3] Implementar endpoint GET `/api/v1/professionals/{id}` em `ProfessionalController.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/`

**Checkpoint**: User Stories 1, 2 e 3 devem funcionar independentemente.

---

## Phase 6: User Story 4 - Atualizar dados do profissional (Priority: P2)

**Goal**: Permitir que o gestor atualize parcialmente os dados de um profissional existente.

**Independent Test**: Após cadastrar um profissional, chamar PATCH `/api/v1/professionals/{id}` alterando apenas o e-mail e verificar que apenas esse campo foi modificado.

### Tests for User Story 4

- [X] T023 [P] [US4] Criar teste unitário `ProfessionalServiceTest.java` em `src/test/java/com/augustoomb/api_barbearia_do_ze/application/professional/` cobrindo atualização parcial, validações e duplicidade de e-mail
- [X] T024 [P] [US4] Adicionar cenários de teste em `ProfessionalControllerIntegrationTest.java` para atualização parcial, campos inválidos, atualização sem campos, nome com mais de 120 caracteres e e-mail com mais de 255 caracteres

### Implementation for User Story 4

- [X] T025 [P] [US4] Criar DTO `UpdateProfessionalRequest.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/` permitindo campos opcionais
- [X] T026 [US4] Implementar método `updateProfessional` em `ProfessionalService.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/` aplicando atualização parcial e validações
- [X] T027 [US4] Implementar endpoint PATCH `/api/v1/professionals/{id}` em `ProfessionalController.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/`

**Checkpoint**: User Stories 1 a 4 devem funcionar independentemente.

---

## Phase 7: User Story 5 - Ativar ou desativar profissional (Priority: P2)

**Goal**: Permitir que o gestor ative ou desative um profissional de forma idempotente.

**Independent Test**: Após cadastrar um profissional, chamar POST `/api/v1/professionals/{id}/deactivate` e verificar `active: false`; chamar duas vezes e verificar idempotência.

### Tests for User Story 5

- [X] T028 [P] [US5] Adicionar cenários de teste em `ProfessionalServiceTest.java` para ativação, desativação e idempotência
- [X] T029 [P] [US5] Adicionar cenários de teste em `ProfessionalControllerIntegrationTest.java` para ativação/desativação e conflito de e-mail na reativação

### Implementation for User Story 5

- [X] T030 [US5] Implementar métodos `activateProfessional` e `deactivateProfessional` em `ProfessionalService.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/application/professional/` com idempotência e validação de unicidade na reativação
- [X] T031 [US5] Implementar endpoints POST `/api/v1/professionals/{id}/activate` e POST `/api/v1/professionals/{id}/deactivate` em `ProfessionalController.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/`

**Checkpoint**: Todas as user stories devem estar independentemente funcionais.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Ajustes finais, cobertura de testes, validação de performance e garantia de qualidade.

- [X] T032 [P] Executar `./mvnw test` e garantir que todos os testes unitários e de integração passam
- [X] T033 [P] Executar `./mvnw verify` para validar cobertura e quality gates
- [X] T034 Executar cenários do `quickstart.md` manualmente via curl para validação ponta a ponta
- [X] T035 Executar cenário de validação de performance para SC-003: cadastrar 100 profissionais e verificar se GET `/api/v1/professionals` responde em menos de 2 segundos
- [X] T036 Revisar `GlobalExceptionHandler.java` em `src/main/java/com/augustoomb/api_barbearia_do_ze/infrastructure/web/` garantindo que `DuplicateProfessionalEmailException` e `ProfessionalNotFoundException` retornam os status HTTP corretos (409 e 404)
- [X] T037 Verificar formatação e estilo de código com as ferramentas do projeto

---

## Phase 9: Convergence

- [X] T038 Adicionar teste de integração em `ProfessionalControllerIntegrationTest.java` para ativação de profissional inativo com e-mail que conflita com outro profissional ativo, validando retorno HTTP 409, per US5/AC4 (missing)
- [X] T039 Renomear teste `shouldRejectActivationWhenEmailConflicts` em `ProfessionalControllerIntegrationTest.java` para refletir que cobre atualização (`PATCH`) com conflito de e-mail, per US4/AC2 e T024 (partial)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode iniciar imediatamente.
- **Foundational (Phase 2)**: Depende da conclusão do Setup — BLOQUEIA todas as user stories.
- **User Stories (Phase 3 a 7)**: Todas dependem da conclusão da Foundational.
  - As user stories podem prosseguir em paralelo (se houver capacidade).
  - Ou sequencialmente em ordem de prioridade (P1 → P2).
- **Polish (Phase 8)**: Depende da conclusão de todas as user stories desejadas.

### User Story Dependencies

- **User Story 1 (P1)**: Pode iniciar após Foundational — sem dependências de outras stories.
- **User Story 2 (P1)**: Pode iniciar após Foundational — sem dependências de outras stories.
- **User Story 3 (P2)**: Pode iniciar após Foundational — sem dependências de outras stories.
- **User Story 4 (P2)**: Pode iniciar após Foundational — sem dependências de outras stories.
- **User Story 5 (P2)**: Pode iniciar após Foundational — sem dependências de outras stories.

### Within Each User Story

- Testes (quando incluídos) devem ser escritos primeiro e FALHAR antes da implementação.
- Models antes de services.
- Services antes de endpoints.
- Implementação core antes da integração.
- Story completa antes de avançar para a próxima prioridade.

### Parallel Opportunities

- Todas as tarefas de Setup marcadas [P] podem rodar em paralelo.
- Todas as tarefas de Foundational marcadas [P] podem rodar em paralelo (dentro da Phase 2).
- Uma vez completa a Foundational, todas as user stories podem iniciar em paralelo (se houver capacidade de equipe).
- Todos os testes de uma user story marcados [P] podem rodar em paralelo.
- DTOs, mapper e response dentro da US1 marcados [P] podem ser criados em paralelo.

---

## Parallel Example: User Story 1

```bash
# Tarefas paralelas de testes da User Story 1:
Task: "T010 [P] [US1] Criar teste unitário ProfessionalEntityTest.java..."
Task: "T011 [P] [US1] Criar teste de integração ProfessionalControllerIntegrationTest.java..."

# Tarefas paralelas de criação de DTOs/mapper da User Story 1:
Task: "T012 [P] [US1] Criar DTO CreateProfessionalRequest.java..."
Task: "T013 [P] [US1] Criar DTO ProfessionalResponse.java..."
Task: "T014 [P] [US1] Criar mapper ProfessionalMapper.java..."
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Testar User Story 1 independentemente
5. Deploy/demo se pronto

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Testar independentemente → Deploy/Demo (MVP!)
3. Add User Story 2 → Testar independentemente → Deploy/Demo
4. Add User Story 3 → Testar independentemente → Deploy/Demo
5. Add User Story 4 → Testar independentemente → Deploy/Demo
6. Add User Story 5 → Testar independentemente → Deploy/Demo
7. Cada story agrega valor sem quebrar as anteriores

### Parallel Team Strategy

Com múltiplos desenvolvedores:

1. Equipe completa Setup + Foundational juntos
2. Uma vez Foundacional pronta:
   - Desenvolvedor A: User Story 1
   - Desenvolvedor B: User Story 2
   - Desenvolvedor C: User Story 3
   - Desenvolvedor D: User Story 4
   - Desenvolvedor E: User Story 5
3. Stories completam e integram independentemente

---

## Notes

- [P] tasks = arquivos diferentes, sem dependências.
- [Story] label mapeia a tarefa para a user story específica, garantindo rastreabilidade.
- Cada user story deve ser completável e testável independentemente.
- Verifique que os testes falham antes de implementar.
- Commit após cada tarefa ou grupo lógico.
- Pare em qualquer checkpoint para validar a story independentemente.
- Evite: tarefas vagas, conflitos no mesmo arquivo, dependências cross-story que quebrem a independência.
