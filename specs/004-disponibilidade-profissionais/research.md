# Research Notes: Disponibilidade dos Profissionais

**Feature**: Disponibilidade dos Profissionais  
**Date**: 2026-09-29  
**Goal**: Consolidar decisões técnicas e padrões adotados para o plano de implementação.

## Decisions

### 1. Representação do dia da semana

- **Decision**: Utilizar o enum `java.time.DayOfWeek` do JDK ou um enum próprio equivalente (`MONDAY`, `TUESDAY`, ..., `SUNDAY`).
- **Rationale**: Evita reinvenciar a roda, é internacionalmente reconhecido e integra-se naturalmente com `java.time`. O armazenamento no banco será como inteiro (`1` a `7`) ou string, conforme já adotado pelo time.
- **Alternatives considered**: String livre no banco — rejeitada por permitir valores inválidos e dificultar ordenação.

### 2. Tipo dos horários de início e término

- **Decision**: Utilizar `java.time.LocalTime` para `startTime` e `endTime`.
- **Rationale**: Representa corretamente um horário do dia sem data. O JPA 3.x mapeia `LocalTime` para `TIME` do PostgreSQL sem necessidade de conversores customizados.
- **Alternatives considered**: `OffsetTime` — rejeitado por não haver necessidade de fuso horário neste domínio.

### 3. Validação de sobreposição de períodos

- **Decision**: Implementar a regra na camada de Service/Domain: dois períodos se sobrepõem se `startTime < otherEndTime && endTime > otherStartTime` no mesmo dia e mesmo profissional. Períodos consecutivos (`endTime == otherStartTime`) são permitidos.
- **Rationale**: Alinha-se com a clarificação da feature e é a forma mais robusta de garantir integridade, podendo ser reforçada por constraint de banco se desejado.
- **Alternatives considered**: Verificação apenas no banco — rejeitada por dificultar mensagens de erro amigáveis e testes unitários.

### 4. Separação entre entidade de domínio e DTO de API

- **Decision**: Manter `AvailabilityEntity` como entidade JPA no domínio e `AvailabilityRequest`/`AvailabilityResponse`/`AvailabilityUpdateRequest` como records DTOs na camada web, com conversão via MapStruct.
- **Rationale**: Cumpre o Princípio I da Constitution (DTOs na fronteira, domínio desacoplado) e facilita testes unitários da regra de negócio sem carregar o framework web.

### 5. Tratamento de exceções

- **Decision**: Criar exceções de domínio específicas (`AvailabilityNotFoundException`, `InvalidAvailabilityPeriodException`, `OverlappingAvailabilityException`, `ProfessionalNotFoundException`) e traduzi-las em respostas HTTP padronizadas pelo `GlobalExceptionHandler` existente.
- **Rationale**: Mantém a separação entre domínio e infraestrutura HTTP e garante mensagens consistentes aos consumidores da API.

### 6. Persistência e migrations

- **Decision**: Criar uma tabela `availability` via Flyway com chave estrangeira para `professional` e índice composto em `(professional_id, day_of_week, start_time, end_time)`.
- **Rationale**: Suporta a regra de conflito de forma eficiente e garante integridade referencial. A migration será reversível com instrução `DROP TABLE` no `down`.

## Unknowns Resolved

Nenhum `NEEDS CLARIFICATION` identificado no Technical Context. Todas as decisões derivam da Constitution e das clarificações já registradas em `spec.md`.
