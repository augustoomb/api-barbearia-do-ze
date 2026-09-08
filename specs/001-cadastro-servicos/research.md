# Research: Cadastro de Serviços

**Feature**: Cadastro de Serviços  
**Date**: 2026-09-08

## Decisions

### 1. Persistência do preço

**Decision**: Armazenar o preço como `NUMERIC(10,2)` no PostgreSQL e mapear para `BigDecimal` no Java.

**Rationale**: `BigDecimal` evita erros de arredondamento inerentes a ponto flutuante, e `NUMERIC(10,2)` garante precisão decimal conforme a regra de negócio (até duas casas decimais).

**Alternatives considered**: `DOUBLE PRECISION` — rejeitado por introduzir imprecisão em cálculos monetários.

### 2. Normalização do nome para unicidade

**Decision**: Aplicar normalização no momento da comparação de unicidade: remover acentos, converter para minúsculas e remover espaços no início/fim. O nome original informado pelo usuário será preservado para exibição.

**Rationale**: Permite reaproveitar nomes inativos sem permitir duplicidades perceptíveis para o usuário.

**Alternatives considered**: Criar coluna separada `normalized_name` — rejeitado por adicionar complexidade desnecessária; a normalização pode ser feita em tempo de validação.

### 3. Atualização parcial

**Decision**: Utilizar requisição PATCH semântica, permitindo que apenas os campos enviados sejam atualizados.

**Rationale**: Alinhado à clarificação da feature; reduz sobrescrita acidental e melhora a experiência do gestor.

**Alternatives considered**: PUT completo — rejeitado por exigir todos os campos e não refletir a decisão de negócio.

### 4. Status ativo/inativo

**Decision**: Representar o status como `BOOLEAN` no banco (`active`) ou `VARCHAR` com constraint (`ACTIVE`, `INACTIVE`). Nesta feature, opta-se por `BOOLEAN` por simplicidade, mapeado para enum `ServiceStatus` no domínio.

**Rationale**: Apenas dois estados são necessários; booleano é suficiente e eficiente. O enum no domínio preserva legibilidade.

**Alternatives considered**: Tabela de status separada — rejeitado por over-engineering para apenas dois estados.

### 5. Duração em minutos

**Decision**: Armazenar como `INTEGER` no banco e validar como maior que zero.

**Rationale**: A regra de negócio define duração em minutos inteiros positivos; `INTEGER` é adequado.

### 6. Testes

**Decision**: Testes unitários para `ServiceEntity` e `ServiceService`; teste de integração para `ServiceController` com banco H2 em memória.

**Rationale**: Cobertura das regras de negócio isoladas e validação ponta a ponta dos contratos da API, conforme princípio III da Constitution.

## Open Questions

Nenhuma questão em aberto identificada para esta feature.
