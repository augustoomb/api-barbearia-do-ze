---

description: "Lista de tarefas para implementação da feature Cadastro de Serviços"
---

# Tarefas: Cadastro de Serviços

**Entrada**: Documentos de design em `/specs/001-cadastro-servicos/`

**Pré-requisitos**: plan.md, spec.md, data-model.md, contracts/api.md, quickstart.md

**Testes**: As tarefas incluem testes unitários e de integração conforme exigido pela Constitution do projeto.

**Organização**: As tarefas são agrupadas por história de usuário para permitir implementação e testes independentes de cada uma.

## Formato: `[ID] [P?] [História] Descrição`

- **[P]**: Pode executar em paralelo (arquivos diferentes, sem dependências)
- **[História]**: Qual história de usuário a tarefa pertence (ex.: US1, US2, US3)

## Convenções de Caminho

- Pacote base: `com.barbeariadose.api`
- Raiz do código fonte: `src/main/java/com/barbeariadose/api/`
- Raiz dos testes: `src/test/java/com/barbeariadose/api/`
- Migrations: `src/main/resources/db/migration/`

---

## Fase 1: Setup (Infraestrutura Compartilhada)

**Propósito**: Verificar e preparar a estrutura do projeto e as dependências.

- [ ] T001 Verificar se a estrutura do projeto existe em `src/main/java/com/barbeariadose/api/` e criar diretórios ausentes: `domain/service/`, `application/service/`, `infrastructure/web/` e os respectivos diretórios de teste
- [ ] T002 Verificar se o `pom.xml` inclui as dependências necessárias: Spring Web, Spring Data JPA, Spring Validation, Flyway, driver PostgreSQL, H2, Lombok e MapStruct

---

## Fase 2: Fundação (Pré-requisitos Bloqueantes)

**Propósito**: Domínio central, persistência, DTOs e infraestrutura transversal que DEVEM estar completos antes das histórias de usuário.

**⚠️ CRÍTICO**: Nenhum trabalho de história de usuário pode começar até esta fase estar completa.

- [ ] T003 [P] Criar migration Flyway `src/main/resources/db/migration/V1__create_services_table.sql` com a tabela `services` conforme data-model.md, incluindo instrução de rollback `DROP TABLE IF EXISTS services;`
- [ ] T005 [P] Criar `src/main/java/com/barbeariadose/api/domain/service/ServiceEntity.java` entidade JPA com todos os campos e timestamps de ciclo de vida, usando campo `boolean active`
- [ ] T006 Criar `src/main/java/com/barbeariadose/api/domain/service/ServiceRepository.java` interface Spring Data JPA com o método de query `findAllByActiveTrue()` para suportar futuras features de agendamento (depende de T005)
- [ ] T007 [P] Criar `src/main/java/com/barbeariadose/api/domain/service/NameNormalizer.java` utilitário para normalizar nomes de serviço (minúsculas, remover acentos, trim)
- [ ] T008 [P] Criar exceções de domínio em `src/main/java/com/barbeariadose/api/domain/service/`: `ServiceNotFoundException`, `DuplicateServiceNameException` e `InvalidServiceException`
- [ ] T009 [P] Criar DTOs de requisição/resposta em `src/main/java/com/barbeariadose/api/application/service/`: `CreateServiceRequest.java`, `UpdateServiceRequest.java` e `ServiceResponse.java`
- [ ] T010 Criar `src/main/java/com/barbeariadose/api/application/service/ServiceMapper.java` para converter entre Entity, Request e Response DTOs (depende de T005 e T009)
- [ ] T011 [P] Criar `src/main/java/com/barbeariadose/api/infrastructure/web/ApiResponse.java` envelope padronizado de resposta
- [ ] T012 [P] Criar `src/main/java/com/barbeariadose/api/infrastructure/web/GlobalExceptionHandler.java` para tratar validação, não encontrado, conflito e erros genéricos com respostas padronizadas

**Checkpoint**: Fundação pronta — a implementação das histórias de usuário pode começar em paralelo.

---

## Fase 3: História de Usuário 1 - Cadastrar novo serviço (Prioridade: P1) 🎯 MVP

**Objetivo**: Permitir que gestores criem novos serviços da barbearia com validação e status ativo por padrão.

**Teste Independente**: Enviar uma requisição POST para `/api/v1/services` e verificar que o serviço foi persistido com `active: true` e pode ser consultado.

### Testes para História de Usuário 1

- [ ] T013 [P] [US1] Escrever testes unitários para `ServiceService.create()` em `src/test/java/com/barbeariadose/api/application/service/ServiceServiceTest.java` cobrindo criação válida, nome em branco, nome duplicado normalizado, duração zero/negativa e preço negativo
- [ ] T014 [P] [US1] Escrever testes de integração para `POST /api/v1/services` em `src/test/java/com/barbeariadose/api/infrastructure/web/ServiceControllerIntegrationTest.java` cobrindo respostas 201, 400 e 409

### Implementação para História de Usuário 1

- [ ] T015 [US1] Implementar `ServiceService.create()` em `src/main/java/com/barbeariadose/api/application/service/ServiceService.java` com todas as validações de negócio
- [ ] T016 [US1] Implementar endpoint `POST /api/v1/services` em `src/main/java/com/barbeariadose/api/infrastructure/web/ServiceController.java`

**Checkpoint**: A História de Usuário 1 deve estar totalmente funcional e testável independentemente.

---

## Fase 4: História de Usuário 2 - Consultar serviços cadastrados (Prioridade: P1)

**Objetivo**: Permitir que gestores listem todos os serviços cadastrados, ativos ou inativos.

**Teste Independente**: Após criar serviços via POST, enviar GET `/api/v1/services` e receber todos os serviços persistidos.

### Testes para História de Usuário 2

- [ ] T017 [P] [US2] Escrever testes unitários para `ServiceService.findAll()` em `ServiceServiceTest.java`
- [ ] T018 [P] [US2] Escrever testes de integração para `GET /api/v1/services` em `ServiceControllerIntegrationTest.java` cobrindo lista populada e lista vazia

### Implementação para História de Usuário 2

- [ ] T019 [US2] Implementar `ServiceService.findAll()` em `ServiceService.java`
- [ ] T020 [US2] Implementar endpoint `GET /api/v1/services` em `ServiceController.java`

**Checkpoint**: As Histórias de Usuário 1 e 2 devem funcionar independentemente.

---

## Fase 5: História de Usuário 3 - Consultar serviço específico (Prioridade: P2)

**Objetivo**: Permitir que gestores consultem os detalhes de um serviço pelo identificador.

**Teste Independente**: Enviar GET `/api/v1/services/{id}` e receber 200 para serviço existente ou 404 para serviço inexistente.

### Testes para História de Usuário 3

- [ ] T021 [P] [US3] Escrever testes unitários para `ServiceService.findById()` em `ServiceServiceTest.java`
- [ ] T022 [P] [US3] Escrever testes de integração para `GET /api/v1/services/{id}` em `ServiceControllerIntegrationTest.java`

### Implementação para História de Usuário 3

- [ ] T023 [US3] Implementar `ServiceService.findById()` em `ServiceService.java`
- [ ] T024 [US3] Implementar endpoint `GET /api/v1/services/{id}` em `ServiceController.java`

**Checkpoint**: As Histórias de Usuário 1, 2 e 3 devem funcionar independentemente.

---

## Fase 6: História de Usuário 4 - Atualizar serviço (Prioridade: P2)

**Objetivo**: Permitir que gestores atualizem parcialmente os dados de um serviço sem alterar o status.

**Teste Independente**: Enviar PATCH `/api/v1/services/{id}` apenas com `price` e verificar que o serviço foi atualizado enquanto os demais campos permanecem inalterados.

### Testes para História de Usuário 4

- [ ] T025 [P] [US4] Escrever testes unitários para `ServiceService.update()` em `ServiceServiceTest.java` cobrindo atualizações parciais, requisição em branco, nome duplicado, duração inválida e preço inválido
- [ ] T026 [P] [US4] Escrever testes de integração para `PATCH /api/v1/services/{id}` em `ServiceControllerIntegrationTest.java`

### Implementação para História de Usuário 4

- [ ] T027 [US4] Implementar `ServiceService.update()` em `ServiceService.java` com lógica de atualização parcial e validação apenas dos campos informados
- [ ] T028 [US4] Implementar endpoint `PATCH /api/v1/services/{id}` em `ServiceController.java`

**Checkpoint**: As Histórias de Usuário 1–4 devem funcionar independentemente.

---

## Fase 7: História de Usuário 5 - Ativar ou desativar serviço (Prioridade: P2)

**Objetivo**: Permitir que gestores ativem ou desativem um serviço por meio de endpoints dedicados, com idempotência e verificação de conflito de nome na ativação.

**Teste Independente**: Enviar POST `/api/v1/services/{id}/deactivate` e verificar `active: false`; enviar POST `/api/v1/services/{id}/activate` duas vezes e verificar que ambas retornam sucesso.

### Testes para História de Usuário 5

- [ ] T029 [P] [US5] Escrever testes unitários para `ServiceService.activate()` e `ServiceService.deactivate()` em `ServiceServiceTest.java` cobrindo transições de status, idempotência e conflito de nome na ativação
- [ ] T030 [P] [US5] Escrever testes de integração para os endpoints de ativação/desativação em `ServiceControllerIntegrationTest.java`

### Implementação para História de Usuário 5

- [ ] T031 [US5] Implementar `ServiceService.activate()` e `ServiceService.deactivate()` em `ServiceService.java` com idempotência e validação de conflito
- [ ] T032 [US5] Implementar endpoints `POST /api/v1/services/{id}/activate` e `POST /api/v1/services/{id}/deactivate` em `ServiceController.java`

**Checkpoint**: Todas as histórias de usuário devem estar funcionalmente independentes.

---

## Fase 8: Polimento e Cuidados Transversais

**Propósito**: Quality gates, documentação e validação final.

- [ ] T033 [P] Executar todos os testes unitários e de integração com `./mvnw test` e corrigir falhas
- [ ] T034 [P] Executar os cenários de validação do `quickstart.md` contra uma instância local em execução
- [ ] T035 [P] Verificar cobertura de código e adicionar testes ausentes se necessário
- [ ] T035b [P] Validar que `GET /api/v1/services` retorna uma lista de 100 serviços em menos de 2 segundos usando um teste de integração ou de performance
- [ ] T036 Verificar se as anotações OpenAPI/SpringDoc estão presentes nos endpoints de `ServiceController.java`
- [ ] T037 Revisar logs estruturados e garantir que `trace-id`/`correlation-id` sejam propagados nos logs do controller e do service
- [ ] T038 Executar `./mvnw verify` ou quality gate equivalente para confirmar que o build passa

---

## Dependências e Ordem de Execução

### Dependências entre Fases

- **Fase 1 (Setup)**: Sem dependências.
- **Fase 2 (Fundação)**: Depende do Setup — bloqueia todas as histórias de usuário.
- **Fases 3–7 (Histórias de Usuário)**: Todas dependem da Fase 2.
  - Executar em ordem de prioridade (P1 → P2) ou em paralelo se houver capacidade.
- **Fase 8 (Polimento)**: Depende de todas as histórias de usuário desejadas estarem completas.

### Dependências entre Histórias de Usuário

- **História 1 (P1)**: Sem dependências de outras histórias; entrega o MVP.
- **História 2 (P1)**: Depende apenas da fundação; reutiliza ServiceEntity e repository.
- **História 3 (P2)**: Depende apenas da fundação; reutiliza componentes existentes.
- **História 4 (P2)**: Depende apenas da fundação; reutiliza validações e DTOs.
- **História 5 (P2)**: Depende apenas da fundação; reutiliza repository e lógica de nome normalizado.

### Dentro de Cada História de Usuário

- Testes unitários e de integração podem ser escritos em paralelo após a fundação pronta.
- Implementação do service antes do endpoint do controller.
- Implementação do endpoint antes dos testes de integração passarem.

### Oportunidades de Paralelismo

- As tarefas fundamentais T003, T005, T007, T008, T009, T011 e T012 podem rodar em paralelo; T006 depende de T005 e T010 depende de T005 e T009.
- Os testes de cada história de usuário (T013–T014, T017–T018, etc.) podem rodar em paralelo entre si.
- Histórias de usuário diferentes podem ser implementadas em paralelo por desenvolvedores diferentes após a fundação completa.
- As tarefas de polimento T033–T038 podem rodar majoritariamente em paralelo.

---

## Exemplo Paralelo: História de Usuário 1

```bash
# Testes unitário e de integração podem ser escritos em paralelo:
Tarefa: "Escrever testes unitários para ServiceService.create() em ServiceServiceTest.java"
Tarefa: "Escrever testes de integração para POST /api/v1/services em ServiceControllerIntegrationTest.java"

# Depois implementar service e controller (dependência sequencial):
Tarefa: "Implementar ServiceService.create() em ServiceService.java"
Tarefa: "Implementar endpoint POST /api/v1/services em ServiceController.java"
```

---

## Estratégia de Implementação

### MVP Primeiro (Apenas História 1)

1. Completar Fase 1: Setup.
2. Completar Fase 2: Fundação (crítica — bloqueia todas as histórias).
3. Completar Fase 3: História 1 (criar serviço).
4. **PARAR E VALIDAR**: Testar a História 1 independentemente usando o cenário 1 do quickstart.
5. Fazer deploy/demo se estiver pronto.

### Entrega Incremental

1. Completar Setup + Fundação → Fundação pronta.
2. Adicionar História 1 → Testar independentemente → Deploy/Demo (MVP).
3. Adicionar História 2 → Testar independentemente → Deploy/Demo.
4. Adicionar Histórias 3, 4, 5 → Testar independentemente → Deploy/Demo.
5. Cada história agrega valor sem quebrar as anteriores.

### Estratégia de Equipe Paralela

Com múltiplos desenvolvedores:

1. A equipe completa Setup + Fundação junta.
2. Após a fundação pronta:
   - Desenvolvedor A: História 1
   - Desenvolvedor B: História 2
   - Desenvolvedor C: História 3
   - Desenvolvedor D: História 4
   - Desenvolvedor E: História 5
3. As histórias são completadas e integradas independentemente.

---

## Observações

- Tarefas `[P]` = arquivos diferentes, sem dependências.
- Rótulo `[História]` mapeia a tarefa para uma história específica para rastreabilidade.
- Cada história de usuário deve ser completável e testável independentemente.
- Verificar que os testes falham antes de implementar (red-green-refactor recomendado, mas não obrigatório).
- Commit após cada tarefa ou grupo lógico.
- Parar em qualquer checkpoint para validar uma história independentemente.
- Evitar tarefas vagas, conflitos no mesmo arquivo e dependências entre histórias que quebrem a independência.
