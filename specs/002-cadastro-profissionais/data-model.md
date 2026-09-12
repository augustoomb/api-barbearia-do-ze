# Modelo de Dados: Cadastro de Profissionais

## Entidade: Profissional (`Professional`)

Representa um membro da equipe da barbearia responsável pela realização dos serviços.

| Campo | Tipo | Restrições | Descrição |
|-------|------|------------|-----------|
| id | UUID (auto-gerado) | PK, not null | Identificador único gerado pelo sistema |
| name | VARCHAR(120) | Not null, trim aplicado | Nome do profissional exibido aos usuários |
| email | VARCHAR(255) | Not null, único em todo o cadastro | Endereço de e-mail do profissional |
| active | BOOLEAN | Not null, default true | Indica se o profissional está ativo |
| createdAt | TIMESTAMP | Not null, auto-gerado | Data/hora de criação do registro |
| updatedAt | TIMESTAMP | Not null, auto-atualizado | Data/hora da última atualização |

## Regras de Validação

- `name` é obrigatório, não pode estar em branco após o trim e deve ter no máximo 120 caracteres.
- `email` é obrigatório, deve possuir formato válido, deve ter no máximo 255 caracteres e deve ser único em todo o cadastro.
- A unicidade de `email` é verificada usando comparação normalizada (case-insensitive, espaços no início/fim removidos).
- O campo `active` é somente leitura nas operações de cadastro e atualização parcial; só pode ser alterado pelas operações dedicadas de ativação/desativação.

## Transições de Estado

```
[CRIADO] --(active=true)--> [ATIVO]
[ATIVO]  --(desativar)--> [INATIVO]
[INATIVO] --(ativar, sem conflito de e-mail)--> [ATIVO]
```

- A ativação de um profissional inativo é recusada se outro profissional já possuir o mesmo e-mail normalizado.
- Ativação/desativação são idempotentes.

## Relacionamentos

- Nenhum relacionamento direto com outras entidades nesta feature.
- Features futuras de agendamento referenciarão `Professional` pelo `id` e filtrarão apenas registros com `active = true` ao atribuir novos agendamentos.

## Schema do Banco de Dados (Migration Flyway)

```sql
CREATE TABLE professionals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_professionals_active ON professionals(active);
```

> Nota: A constraint `UNIQUE` sobre o e-mail garante unicidade no nível do banco, considerando a comparação padrão do PostgreSQL (case-sensitive). A normalização case-insensitive para unicidade deve ser aplicada pela camada de aplicação, que deve rejeitar e-mails duplicados em qualquer variação de maiúsculas/minúsculas.
