# Data Model: Disponibilidade dos Profissionais

## Entities

### Availability

Representa um período de disponibilidade semanal de um profissional.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | UUID / Long | PK, auto-generated | Identificador único do período. |
| `professionalId` | UUID / Long | FK → `professional(id)`, NOT NULL | Profissional ao qual o período pertence. |
| `dayOfWeek` | Integer / Enum | NOT NULL | Dia da semana (`1`=Monday ... `7`=Sunday). |
| `startTime` | LocalTime | NOT NULL | Horário de início do período. |
| `endTime` | LocalTime | NOT NULL | Horário de término do período. |
| `createdAt` | Instant / Timestamp | NOT NULL, auto-generated | Data/hora de criação do registro. |
| `updatedAt` | Instant / Timestamp | NOT NULL, auto-generated | Data/hora da última atualização. |

### Professional

Entidade existente no sistema, referenciada pela disponibilidade.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | UUID / Long | PK | Identificador do profissional. |
| ... | ... | ... | Campos já definidos na feature anterior. |

## Relationships

- **Professional 1:N Availability**: Um profissional pode ter zero ou mais períodos de disponibilidade. Um período de disponibilidade pertence a exatamente um profissional.

## Validation Rules

1. **Existência do profissional**: `professionalId` deve referenciar um profissional existente.
2. **Horário válido**: `endTime` deve ser posterior a `startTime`.
3. **Sem sobreposição**: Não pode existir outro período do mesmo profissional no mesmo `dayOfWeek` cujo intervalo `[startTime, endTime)` se sobreponha ao novo/alterado período. Períodos consecutivos (`endTime == otherStartTime`) são permitidos.
4. **Profissional imutável na alteração**: O `professionalId` de um período existente não pode ser alterado.

## Database Schema

```sql
CREATE TABLE availability (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    professional_id UUID NOT NULL REFERENCES professional(id),
    day_of_week     SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_availability_time_range CHECK (end_time > start_time)
);

CREATE INDEX idx_availability_professional_day
    ON availability(professional_id, day_of_week);
```

### Migration

- **File**: `src/main/resources/db/migration/V3__create_availability_table.sql`
- **Type**: Versioned migration
- **Rollback**: `DROP TABLE availability;`

## State Transitions

O período de disponibilidade não possui máquina de estado complexa. Seu ciclo de vida é:

1. **Created**: após validação bem-sucedida no cadastro.
2. **Updated**: após validação bem-sucedida na alteração (exceto `professionalId`).
3. **Deleted**: remoção permanente do registro.
