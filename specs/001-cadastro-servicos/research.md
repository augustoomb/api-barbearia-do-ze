# Pesquisa: Cadastro de Serviços

**Feature**: Cadastro de Serviços  
**Data**: 2026-09-08

## Decisões

### 1. Persistência do preço

**Decisão**: Armazenar o preço como `NUMERIC(10,2)` no PostgreSQL e mapear para `BigDecimal` no Java.

**Racional**: `BigDecimal` evita erros de arredondamento inerentes ao ponto flutuante, e `NUMERIC(10,2)` garante a precisão decimal conforme a regra de negócio (até duas casas decimais).

**Alternativas consideradas**: `DOUBLE PRECISION` — rejeitado por introduzir imprecisão em cálculos monetários.

### 2. Normalização do nome para unicidade

**Decisão**: Aplicar normalização no momento da comparação de unicidade: remover acentos, converter para minúsculas e remover espaços no início/fim. O nome original informado pelo usuário será preservado para exibição.

**Racional**: Permite reaproveitar nomes inativos sem permitir duplicidades perceptíveis para o usuário.

**Alternativas consideradas**: Criar coluna separada `normalized_name` — rejeitado por adicionar complexidade desnecessária; a normalização pode ser feita em tempo de validação.

### 3. Atualização parcial

**Decisão**: Utilizar requisição PATCH semântica, permitindo que apenas os campos enviados sejam atualizados.

**Racional**: Alinhado à clarificação da feature; reduz sobrescrita acidental e melhora a experiência do gestor.

**Alternativas consideradas**: PUT completo — rejeitado por exigir todos os campos e não refletir a decisão de negócio.

### 4. Status ativo/inativo

**Decisão**: Representar o status como `BOOLEAN` no banco (`active`) e como `boolean` no domínio.

**Racional**: Apenas dois estados são necessários; booleano é suficiente e eficiente tanto no banco quanto no código, evitando conversões desnecessárias.

**Alternativas consideradas**:
- Enum `ServiceStatus` no domínio — rejeitado para manter alinhamento direto com a coluna booleana e reduzir complexidade.
- `VARCHAR` com constraint no banco — rejeitado por não agregar valor para apenas dois estados.

### 5. Duração em minutos

**Decisão**: Armazenar como `INTEGER` no banco e validar como maior que zero.

**Racional**: A regra de negócio define duração em minutos inteiros positivos; `INTEGER` é adequado.

### 6. Testes

**Decisão**: Testes unitários para `ServiceEntity` e `ServiceService`; teste de integração para `ServiceController` com banco H2 em memória.

**Racional**: Cobertura das regras de negócio isoladas e validação ponta a ponta dos contratos da API, conforme o Princípio III da Constitution.

## Questões em Aberto

Nenhuma questão em aberto identificada para esta feature.
