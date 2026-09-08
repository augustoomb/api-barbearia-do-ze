
# Feature Specification: Cadastro de Serviços

**Feature Branch**: `001-feature-001-cadastro-serviços`

**Created**: 2026-09-07

**Status**: Draft

**Input**: User description: "Quero criar a Feature 001 — Cadastro de Serviços. O sistema deve permitir que a barbearia cadastre os serviços que oferece aos clientes. Um serviço deve possuir: nome; descrição opcional; duração estimada em minutos; preço; status indicando se está ativo ou inativo. Deve ser possível: cadastrar um serviço; consultar os serviços cadastrados; consultar um serviço específico; atualizar um serviço; ativar ou desativar um serviço. Regras de negócio: o nome é obrigatório; o nome deve ser único entre os serviços ativos; a duração deve ser maior que zero; o preço não pode ser negativo; serviços inativos não devem aparecer como disponíveis para novos agendamentos. Quero que você produza a especificação focando no comportamento e nas regras de negócio, sem definir detalhes de implementação. Use português do Brasil (pt-BR)."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Cadastrar novo serviço (Priority: P1)

Como gestor da barbearia, quero cadastrar os serviços oferecidos para que eles possam ser utilizados nos agendamentos futuros.

**Why this priority**: Sem o cadastro de serviços, não é possível montar a oferta da barbearia nem viabilizar os agendamentos. É a base das operações.

**Independent Test**: Pode ser testado independentemente ao cadastrar um serviço completo e verificar que ele fica disponível para consulta imediatamente.

**Acceptance Scenarios**:

1. **Given** que o gestor informou nome, duração e preço válidos, **When** ele solicita o cadastro, **Then** o serviço é criado com status ativo e fica disponível para consulta.
2. **Given** que o gestor informou uma descrição opcional, **When** ele solicita o cadastro, **Then** o serviço é criado mantendo a descrição informada.
3. **Given** que o gestor informou nome vazio, **When** ele solicita o cadastro, **Then** o cadastro é recusado com mensagem indicando que o nome é obrigatório.
4. **Given** que já existe um serviço ativo com o mesmo nome, **When** o gestor tenta cadastrar outro serviço com esse nome, **Then** o cadastro é recusado com mensagem indicando que o nome deve ser único entre os serviços ativos.
5. **Given** que o gestor informou duração igual a zero ou negativa, **When** ele solicita o cadastro, **Then** o cadastro é recusado com mensagem indicando que a duração deve ser maior que zero.
6. **Given** que o gestor informou preço negativo, **When** ele solicita o cadastro, **Then** o cadastro é recusado com mensagem indicando que o preço não pode ser negativo.

---

### User Story 2 - Consultar serviços cadastrados (Priority: P1)

Como gestor da barbearia, quero consultar a lista de serviços cadastrados para visualizar a oferta atual da barbearia.

**Why this priority**: A consulta permite ao gestor gerenciar a oferta e validar se os serviços estão corretos.

**Independent Test**: Pode ser testado independentemente ao cadastrar serviços e verificar que a consulta retorna os dados esperados.

**Acceptance Scenarios**:

1. **Given** que existem serviços cadastrados, **When** o gestor solicita a consulta da lista, **Then** o sistema retorna todos os serviços cadastrados com seus dados.
2. **Given** que não existem serviços cadastrados, **When** o gestor solicita a consulta da lista, **Then** o sistema retorna uma lista vazia.

---

### User Story 3 - Consultar serviço específico (Priority: P2)

Como gestor da barbearia, quero consultar os detalhes de um serviço específico para verificar ou revisar suas informações.

**Why this priority**: Permite auditoria e tomada de decisão individual sobre cada serviço.

**Independent Test**: Pode ser testado independentemente ao cadastrar um serviço e consultá-lo por identificador único.

**Acceptance Scenarios**:

1. **Given** que o serviço existe, **When** o gestor solicita a consulta por identificador, **Then** o sistema retorna os dados completos do serviço.
2. **Given** que o serviço não existe, **When** o gestor solicita a consulta por identificador, **Then** o sistema informa que o serviço não foi encontrado.

---

### User Story 4 - Atualizar serviço (Priority: P2)

Como gestor da barbearia, quero atualizar as informações de um serviço para manter a oferta sempre correta.

**Why this priority**: Preços, durações e descrições mudam com o tempo; a atualização mantém a oferta alinhada com a realidade.

**Independent Test**: Pode ser testado independentemente ao alterar dados de um serviço existente e verificar que a consulta posterior reflete as mudanças.

**Acceptance Scenarios**:

1. **Given** que o serviço existe e o gestor informa apenas os campos que deseja alterar com valores válidos, **When** ele solicita a atualização, **Then** apenas os campos informados são modificados e a consulta posterior retorna os valores atualizados mantendo os demais inalterados.
2. **Given** que o gestor altera o nome para um valor já utilizado por outro serviço ativo, **When** ele solicita a atualização, **Then** a atualização é recusada com mensagem indicando que o nome deve ser único entre os serviços ativos.
3. **Given** que o gestor informa duração igual a zero ou negativa durante a atualização, **When** ele solicita a atualização, **Then** a atualização é recusada com mensagem indicando que a duração deve ser maior que zero.
4. **Given** que o gestor informa preço negativo durante a atualização, **When** ele solicita a atualização, **Then** a atualização é recusada com mensagem indicando que o preço não pode ser negativo.

---

### User Story 5 - Ativar ou desativar serviço (Priority: P2)

Como gestor da barbearia, quero ativar ou desativar um serviço para controlar quais serviços estão disponíveis para novos agendamentos.

**Why this priority**: Serviços inativos não devem aparecer como disponíveis para novos agendamentos, o que protege a integridade da agenda.

**Independent Test**: Pode ser testado independentemente ao desativar um serviço e verificar que ele não é mais oferecido como disponível para agendamentos.

**Acceptance Scenarios**:

1. **Given** que o serviço está ativo, **When** o gestor solicita a desativação, **Then** o serviço passa para inativo e não aparece como disponível para novos agendamentos.
2. **Given** que o serviço está inativo, **When** o gestor solicita a ativação, **Then** o serviço passa para ativo e passa a aparecer como disponível para novos agendamentos, desde que não haja outro serviço ativo com o mesmo nome.
3. **Given** que o serviço já está no status solicitado, **When** o gestor repete a operação de ativação ou desativação, **Then** o sistema retorna sucesso sem alterar o serviço.
3. **Given** que o gestor tenta ativar um serviço inativo com nome igual ao de outro serviço já ativo, **When** ele solicita a ativação, **Then** a ativação é recusada com mensagem indicando que o nome deve ser único entre os serviços ativos.

---

### Edge Cases

- O que acontece quando dois serviços inativos possuem o mesmo nome? A unicidade do nome deve ser validada apenas entre serviços ativos, portanto dois serviços inativos podem compartilhar o mesmo nome.
- Como o sistema deve tratar a exclusão física de serviços? A exclusão física não faz parte do escopo desta feature; a inativação é o mecanismo para retirar a oferta de serviços.
- O nome de um serviço inativo pode ser reutilizado para um novo serviço ativo? Sim, desde que não exista outro serviço ativo com o mesmo nome.
- Qual o comportamento esperado ao tentar atualizar um serviço inexistente? O sistema deve informar que o serviço não foi encontrado.
- A duração deve ser informada em minutos inteiros ou aceita frações? Será assumido que a duração é informada em minutos e aceita valores inteiros positivos.
- Como a unicidade de nome trata variações como "Corte", "corte" e "Corte "? O sistema deve considerar esses valores como duplicados após normalização, recusando o cadastro, atualização ou ativação que gere conflito.
- O que acontece se o gestor enviar uma atualização sem informar nenhum campo? O sistema deve recusar a operação, pois não há alterações a aplicar.
- A operação de ativar/desativar deve aceitar chamadas repetidas? Sim, a operação deve ser idempotente, retornando sucesso sem modificar o serviço quando ele já estiver no status desejado.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE permitir o cadastro de um novo serviço contendo nome, descrição opcional, duração estimada em minutos, preço e status ativo.
- **FR-002**: O sistema DEVE exigir que o nome do serviço seja informado no cadastro.
- **FR-003**: O sistema DEVE garantir que o nome do serviço seja único entre todos os serviços ativos, considerando uma comparação normalizada que ignore diferenças de maiúsculas/minúsculas, acentos e espaços em branco no início ou no fim do nome.
- **FR-004**: O sistema DEVE rejeitar duração igual a zero ou negativa, exigindo que a duração seja maior que zero.
- **FR-005**: O sistema DEVE rejeitar preço negativo e deve aceitar apenas valores em reais (R$) com até duas casas decimais.
- **FR-006**: O sistema DEVE permitir a consulta da lista de todos os serviços cadastrados.
- **FR-007**: O sistema DEVE permitir a consulta dos dados de um serviço específico por meio de um identificador único.
- **FR-008**: O sistema DEVE permitir a atualização parcial dos dados de um serviço existente, aplicando as regras de validação do cadastro apenas aos campos efetivamente informados, sem permitir a alteração do status.
- **FR-009**: O sistema DEVE permitir ativar ou desativar um serviço existente por meio de uma operação dedicada, aceitando chamadas repetidas para o mesmo status sem retornar erro (idempotente).
- **FR-010**: O sistema DEVE garantir que serviços inativos não apareçam como disponíveis para novos agendamentos.

### Key Entities *(include if feature involves data)*

- **Serviço**: Representa um serviço oferecido pela barbearia. Atributos: nome, descrição opcional, duração estimada em minutos, preço em reais (R$) com até duas casas decimais, status (ativo ou inativo) e identificador único. A unicidade do nome entre serviços ativos é avaliada de forma normalizada (ignorando case, acentos e espaços externos).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: O gestor da barbearia consegue cadastrar um novo serviço em menos de 1 minuto de interação.
- **SC-002**: 100% dos serviços cadastrados respeitam as regras de validação (nome obrigatório, nome único entre ativos, duração maior que zero, preço não negativo).
- **SC-003**: A consulta de serviços retorna o resultado em menos de 2 segundos para listas com até 100 serviços cadastrados.
- **SC-004**: Serviços inativos não são oferecidos como opção em nenhum novo agendamento.
- **SC-005**: O gestor consegue ativar ou desativar um serviço com no máximo 2 ações.

## Clarifications

### Session 2026-09-08

- **Q:** A regra de unicidade do nome entre serviços ativos deve considerar normalização (ignorar maiúsculas/minúsculas, acentos e espaços extras) ou deve ser uma comparação literal/exata? → **A:** Normalizar nome (ignorar case, acentos e espaços externos).
- **Q:** A atualização de um serviço deve permitir alteração parcial de campos ou todos os campos editáveis devem ser informados a cada atualização? → **A:** Atualização parcial (enviar apenas os campos que devem ser alterados).
- **Q:** Durante a atualização parcial de um serviço, o gestor pode alterar o status (ativo/inativo) diretamente, ou essa mudança só pode ocorrer pela operação dedicada de ativar/desativar? → **A:** O status só pode ser alterado pela operação dedicada de ativar/desativar.
- **Q:** Qual deve ser a precisão máxima aceita para o preço de um serviço e qual moeda será utilizada? → **A:** Preço em reais (R$) com até duas casas decimais.
- **Q:** A operação de ativar ou desativar um serviço deve ser idempotente, ou seja, deve aceitar ativar um serviço já ativo (e desativar um já inativo) sem retornar erro? → **A:** Sim, a operação deve ser idempotente.

## Assumptions

- A feature é exclusiva para gestores da barbearia; controle de permissões detalhado será tratado em feature futura.
- O identificador único do serviço é gerado pelo sistema e não precisa ser informado pelo usuário.
- O preço zero é permitido para representar serviços gratuitos ou promocionais, e o preço deve ser informado em reais (R$) com até duas casas decimais.
- A duração é informada em minutos inteiros positivos.
- Serviços inativos continuam aparecendo na lista geral de consulta para fins de histórico e gestão, mas não estão disponíveis para novos agendamentos.
- A exclusão física de serviços não faz parte do escopo desta feature.
