# Modelo de Dados: Cadastro de Serviços

## Entidade: Serviço (`Service`)

Representa um serviço oferecido pela barbearia.

| Campo | Tipo | Restrições | Descrição |
|-------|------|------------|-----------|
| id | UUID / BIGINT (auto-gerado) | PK, not null | Identificador único gerado pelo sistema |
| name | VARCHAR(255) | Not null, trim aplicado, único entre serviços ativos após normalização | Nome do serviço exibido aos usuários |
| description | VARCHAR(1000) | Opcional, nullable | Descrição opcional do serviço |
| durationMinutes | INTEGER | Not null, > 0 | Duração estimada em minutos |
| price | NUMERIC(10,2) | Not null, >= 0 | Preço em Reais (BRL) |
| active | BOOLEAN | Not null, default true | Indica se o serviço está ativo |
| createdAt | TIMESTAMP | Not null, auto-gerado | Data/hora de criação do registro |
| updatedAt | TIMESTAMP | Not null, auto-atualizado | Data/hora da última atualização |

## Regras de Validação

- `name` é obrigatório e não pode estar em branco após o trim.
- A unicidade de `name` é verificada entre os serviços ativos usando comparação normalizada (case-insensitive, acentos ignorados, espaços no início/fim removidos).
- `durationMinutes` deve ser maior que zero.
- `price` deve ser zero ou positivo, com no máximo duas casas decimais.

## Transições de Estado

```
[CRIADO] --(ativo=true)--> [ATIVO]
[ATIVO]  --(desativar)--> [INATIVO]
[INATIVO] --(ativar, sem conflito de nome)--> [ATIVO]
```

- A ativação de um serviço inativo é recusada se outro serviço ativo já possuir o mesmo nome normalizado.
- Ativação/desativação são idempotentes.

## Relacionamentos

- Nenhum relacionamento direto com outras entidades nesta feature.
- Features futuras de agendamento referenciarão `Service` pelo `id` e filtrarão apenas registros com `active = true`.

## Schema do Banco de Dados (Migration Flyway)

```sql
CREATE TABLE services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    duration_minutes INTEGER NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_services_active ON services(active);
```

> Nota: Uma constraint única sobre o nome normalizado entre serviços ativos não pode ser expressa diretamente em DDL porque a normalização é feita na camada de aplicação. A regra deve ser aplicada pelo código.
