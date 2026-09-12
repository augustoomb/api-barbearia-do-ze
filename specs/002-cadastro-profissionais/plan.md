# Plano de Implementação: Cadastro de Profissionais

**Branch**: `003-cadastro-profissionais` | **Data**: 2026-09-11 | **Spec**: [spec.md](./spec.md)

**Entrada**: Especificação da feature em `/specs/002-cadastro-profissionais/spec.md`

## Resumo

Implementar o cadastro e a gestão dos profissionais da barbearia, permitindo criar, consultar, atualizar parcialmente e ativar/desativar profissionais. A solução seguirá a arquitetura em camadas definida na Constitution (Controller, Service, Repository e Domain/Model), utilizará DTOs com validação explícita, Spring Data JPA com PostgreSQL e Flyway para persistência, e será coberta por testes unitários e de integração.

## Contexto Técnico

**Linguagem/Versão**: Java 21 (LTS)

**Dependências Principais**: Spring Boot 4.x, Spring WebMvc, Spring Data JPA, Spring Validation (Bean Validation/Jakarta Validation), Lombok, MapStruct

**Armazenamento**: PostgreSQL (principal), H2 (testes automatizados)

**Testes**: JUnit 5, Mockito, `@SpringBootTest` para testes de integração

**Plataforma Alvo**: Servidor Linux via Docker Compose

**Tipo de Projeto**: web-service (API REST)

**Metas de Performance**: Consulta de lista com até 100 profissionais em menos de 2 segundos (conforme SC-003 do spec)

**Restrições**: Respeitar a stack padronizada da Constitution; todas as alterações de schema via Flyway; não há código sem testes

**Escala/Escopo**: Ambiente de barbearia com dezenas de profissionais; não há necessidade de paginação nesta feature

## Verificação da Constitution

*GATE: Deve passar antes da Pesquisa (Phase 0). Re-verificar após o Design (Phase 1).*

| Princípio | Status | Observação |
|-----------|--------|------------|
| I. Arquitetura em Camadas | ✅ Pass | Controller, Service, Repository e Domain/Model serão utilizados |
| II. Stack Tecnológica Padronizada | ✅ Pass | Java 21, Spring Boot, Spring WebMvc, Spring Data JPA, Spring Validation, PostgreSQL, Flyway, H2 (testes), Lombok |
| III. Qualidade e Testes | ✅ Pass | Testes unitários (JUnit 5 + Mockito) e de integração (`@SpringBootTest`) serão criados |
| IV. Banco de Dados e Migrations | ✅ Pass | Schema via Flyway; migration reversível |
| V. Mensageria e Cache | ✅ Pass | Não aplicável a esta feature (sem eventos assíncronos) |
| VI. Observabilidade e Monitoramento | ✅ Pass | Logs estruturados e endpoints Actuator herdados do projeto |
| VII. Containerização e Deploy | ✅ Pass | Ambiente via Docker Compose já existente |
| VIII. Comunicação e Notificações | ✅ Pass | Não aplicável a esta feature |

**Resultado**: Todos os princípios da Constitution são respeitados. Nenhuma violação justificada.

## Estrutura do Projeto

### Documentação (desta feature)

```text
specs/002-cadastro-profissionais/
├── plan.md              # Este arquivo (saída do comando /speckit.plan)
├── research.md          # Saída da Fase 0 (comando /speckit.plan)
├── data-model.md        # Saída da Fase 1 (comando /speckit.plan)
├── quickstart.md        # Saída da Fase 1 (comando /speckit.plan)
├── contracts/           # Saída da Fase 1 (comando /speckit.plan)
└── tasks.md             # Saída da Fase 2 (comando /speckit.tasks - NÃO criado pelo /speckit.plan)
```

### Código Fonte (raiz do repositório)

```text
src/main/java/com/augustoomb/api_barbearia_do_ze/
├── domain/
│   └── professional/
│       ├── ProfessionalEntity.java
│       ├── ProfessionalRepository.java
│       ├── ProfessionalNotFoundException.java
│       ├── DuplicateProfessionalEmailException.java
│       └── InvalidProfessionalException.java
├── application/
│   └── professional/
│       ├── ProfessionalService.java
│       ├── CreateProfessionalRequest.java
│       ├── UpdateProfessionalRequest.java
│       ├── ProfessionalResponse.java
│       └── ProfessionalMapper.java
├── infrastructure/
│   └── web/
│       ├── ProfessionalController.java
│       └── GlobalExceptionHandler.java (já existente, estendido)
└── config/
    └── ...

src/test/java/com/augustoomb/api_barbearia_do_ze/
├── domain/professional/
│   └── ProfessionalEntityTest.java
├── application/professional/
│   └── ProfessionalServiceTest.java
└── infrastructure/web/
    └── ProfessionalControllerIntegrationTest.java

src/main/resources/
├── db/migration/
│   └── V2__create_professionals_table.sql
└── application.yml
```

**Decisão de Estrutura**: Estrutura monolítica single-project, com separação em camadas conforme a Constitution. A camada `domain` contém a entidade, o repositório (interface) e as exceções de domínio; a camada `application` contém o serviço de aplicação e os DTOs; a camada `infrastructure` contém o controller REST. Testes unitários cobrem domínio e aplicação; testes de integração cobrem o controller.

## Acompanhamento de Complexidade

> **Preencher SOMENTE se a Verificação da Constitution apresentar violações que devem ser justificadas**

Nenhuma violação identificada.
