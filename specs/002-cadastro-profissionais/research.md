# Pesquisa: Cadastro de Profissionais

**Feature**: Cadastro de Profissionais  
**Data**: 2026-09-11

## Decisões

### 1. Persistência do identificador

**Decisão**: Utilizar `UUID` gerado pelo banco (`gen_random_uuid()`) como chave primária da tabela `professionals`.

**Racional**: Alinhado à migration da feature anterior (`services`) e à especificação, que define o identificador como gerado pelo sistema. UUID facilita integrações futuras e evita exposição de sequências numéricas.

**Alternativas consideradas**: `BIGSERIAL` — rejeitado para manter consistência com a entidade `Service` e evitar previsibilidade de IDs.

### 2. Normalização do e-mail para unicidade

**Decisão**: Aplicar normalização no momento da comparação de unicidade: converter o e-mail para minúsculas e remover espaços no início/fim. O e-mail original informado pelo usuário será preservado para exibição.

**Racional**: Garante que variações como "Joao@Barbearia.com" e "joao@barbearia.com" sejam tratadas como duplicatas, conforme a regra de negócio, sem impedir a exibição do formato preferido pelo usuário.

**Alternativas consideradas**: Criar coluna separada `normalized_email` — rejeitado por adicionar complexidade desnecessária; a normalização pode ser feita em tempo de validação, assim como foi feito para o nome do serviço.

### 3. Atualização parcial

**Decisão**: Utilizar requisição PATCH semântica, permitindo que apenas os campos enviados sejam atualizados.

**Racional**: Alinhado à clarificação da feature; reduz sobrescrita acidental e melhora a experiência do gestor.

**Alternativas consideradas**: PUT completo — rejeitado por exigir todos os campos e não refletir a decisão de negócio.

### 4. Status ativo/inativo

**Decisão**: Representar o status como `BOOLEAN` no banco (`active`) e como `boolean` no domínio.

**Racional**: Apenas dois estados são necessários; booleano é suficiente e eficiente tanto no banco quanto no código, evitando conversões desnecessárias.

**Alternativas consideradas**:
- Enum `ProfessionalStatus` no domínio — rejeitado para manter alinhamento direto com a coluna booleana e reduzir complexidade.
- `VARCHAR` com constraint no banco — rejeitado por não agregar valor para apenas dois estados.

### 5. Validação de e-mail

**Decisão**: Utilizar a anotação `@Email` do Jakarta Validation para validar o formato do e-mail, complementada por validações customizadas de unicidade e tamanho máximo na camada de aplicação.

**Racional**: `@Email` é amplamente aceita, testada e integrada ao ecossistema Spring Validation, reduzindo a necessidade de expressões regulares customizadas.

**Alternativas consideradas**: Expressão regular customizada — rejeitada por ser mais propensa a erros e difícil de manter.

### 6. Testes

**Decisão**: Testes unitários para `ProfessionalEntity` e `ProfessionalService`; teste de integração para `ProfessionalController` com banco H2 em memória.

**Racional**: Cobertura das regras de negócio isoladas e validação ponta a ponta dos contratos da API, conforme o Princípio III da Constitution.

## Questões em Aberto

Nenhuma questão em aberto identificada para esta feature.
