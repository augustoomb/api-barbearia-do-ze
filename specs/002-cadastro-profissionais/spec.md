# Feature Specification: Cadastro de Profissionais

**Feature Branch**: `003-cadastro-profissionais`

**Created**: 2026-09-10

**Status**: Draft

**Input**: User description: "Quero criar a Feature 002 — Cadastro de Profissionais. O sistema deve permitir que a barbearia cadastre os profissionais responsáveis pela realização dos serviços. Um profissional deve possuir: identificador; nome; e-mail; status ativo/inativo. Deve ser possível: cadastrar um profissional; consultar profissionais; consultar um profissional específico; atualizar seus dados; ativar ou desativar um profissional. Regras de negócio: nome é obrigatório; e-mail é obrigatório e deve possuir formato válido; e-mail deve ser único; profissionais inativos não podem receber novos agendamentos; um profissional existente não deve ser duplicado apenas por diferença de maiúsculas/minúsculas no e-mail. A funcionalidade deve preparar o sistema para que futuras features possam associar profissionais aos serviços e aos horários de atendimento. Não defina detalhes de implementação nesta etapa. Foque em comportamento, regras de negócio, casos de uso e critérios de aceitação. Use português do Brasil."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Cadastrar novo profissional (Priority: P1)

Como gestor da barbearia, quero cadastrar os profissionais que realizam os serviços para que eles possam ser associados a agendamentos futuros.

**Why this priority**: Sem o cadastro de profissionais, não é possível organizar a agenda nem garantir que os serviços serão atribuídos a quem os executa. É base para as operações de agendamento.

**Independent Test**: Pode ser testado independentemente ao cadastrar um profissional completo e verificar que ele fica disponível para consulta imediatamente.

**Acceptance Scenarios**:

1. **Given** que o gestor informou nome e e-mail válidos, **When** ele solicita o cadastro, **Then** o profissional é criado com status ativo e fica disponível para consulta.
2. **Given** que o gestor informou nome vazio, **When** ele solicita o cadastro, **Then** o cadastro é recusado com mensagem indicando que o nome é obrigatório.
3. **Given** que o gestor informou e-mail vazio, **When** ele solicita o cadastro, **Then** o cadastro é recusado com mensagem indicando que o e-mail é obrigatório.
4. **Given** que o gestor informou e-mail em formato inválido, **When** ele solicita o cadastro, **Then** o cadastro é recusado com mensagem indicando que o e-mail deve possuir formato válido.
5. **Given** que já existe um profissional com o mesmo e-mail, **When** o gestor tenta cadastrar outro profissional com esse e-mail, **Then** o cadastro é recusado com mensagem indicando que o e-mail deve ser único.
6. **Given** que já existe um profissional com e-mail "joao@barbearia.com", **When** o gestor tenta cadastrar outro profissional com e-mail "Joao@Barbearia.com", **Then** o cadastro é recusado por duplicidade, considerando a comparação insensível a maiúsculas/minúsculas.

---

### User Story 2 - Consultar profissionais cadastrados (Priority: P1)

Como gestor da barbearia, quero consultar a lista de profissionais cadastrados para visualizar a equipe disponível.

**Why this priority**: A consulta permite ao gestor gerenciar a equipe, verificar dados cadastrais e planejar a alocação de profissionais nos serviços e horários.

**Independent Test**: Pode ser testado independentemente ao cadastrar profissionais e verificar que a consulta retorna os dados esperados.

**Acceptance Scenarios**:

1. **Given** que existem profissionais cadastrados, **When** o gestor solicita a consulta da lista, **Then** o sistema retorna todos os profissionais cadastrados com seus dados.
2. **Given** que não existem profissionais cadastrados, **When** o gestor solicita a consulta da lista, **Then** o sistema retorna uma lista vazia.

---

### User Story 3 - Consultar profissional específico (Priority: P2)

Como gestor da barbearia, quero consultar os detalhes de um profissional específico para verificar ou revisar suas informações.

**Why this priority**: Permite auditoria individual, correção de dados e tomada de decisão sobre cada profissional.

**Independent Test**: Pode ser testado independentemente ao cadastrar um profissional e consultá-lo por identificador único.

**Acceptance Scenarios**:

1. **Given** que o profissional existe, **When** o gestor solicita a consulta por identificador, **Then** o sistema retorna os dados completos do profissional.
2. **Given** que o profissional não existe, **When** o gestor solicita a consulta por identificador, **Then** o sistema informa que o profissional não foi encontrado.

---

### User Story 4 - Atualizar dados do profissional (Priority: P2)

Como gestor da barbearia, quero atualizar as informações de um profissional para manter os dados da equipe sempre corretos.

**Why this priority**: Nomes e e-mails podem mudar com o tempo; a atualização mantém o cadastro da equipe alinhado com a realidade.

**Independent Test**: Pode ser testado independentemente ao alterar dados de um profissional existente e verificar que a consulta posterior reflete as mudanças.

**Acceptance Scenarios**:

1. **Given** que o profissional existe e o gestor informa apenas os campos que deseja alterar com valores válidos, **When** ele solicita a atualização, **Then** apenas os campos informados são modificados e a consulta posterior retorna os valores atualizados mantendo os demais inalterados.
2. **Given** que o gestor altera o e-mail para um valor já utilizado por outro profissional, **When** ele solicita a atualização, **Then** a atualização é recusada com mensagem indicando que o e-mail deve ser único.
3. **Given** que o gestor informa e-mail em formato inválido durante a atualização, **When** ele solicita a atualização, **Then** a atualização é recusada com mensagem indicando que o e-mail deve possuir formato válido.
4. **Given** que o gestor informa nome vazio durante a atualização, **When** ele solicita a atualização, **Then** a atualização é recusada com mensagem indicando que o nome é obrigatório.

---

### User Story 5 - Ativar ou desativar profissional (Priority: P2)

Como gestor da barbearia, quero ativar ou desativar um profissional para controlar quais profissionais estão disponíveis para novos agendamentos.

**Why this priority**: Profissionais inativos não podem receber novos agendamentos, o que protege a integridade da agenda e evita alocação indevida.

**Independent Test**: Pode ser testado independentemente ao desativar um profissional e verificar que ele não é mais considerado disponível para novos agendamentos.

**Acceptance Scenarios**:

1. **Given** que o profissional está ativo, **When** o gestor solicita a desativação, **Then** o profissional passa para inativo e não pode receber novos agendamentos.
2. **Given** que o profissional está inativo, **When** o gestor solicita a ativação, **Then** o profissional passa para ativo e pode receber novos agendamentos, desde que não haja outro profissional ativo com o mesmo e-mail.
3. **Given** que o profissional já está no status solicitado, **When** o gestor repete a operação de ativação ou desativação, **Then** o sistema retorna sucesso sem alterar o profissional.
4. **Given** que o gestor tenta ativar um profissional inativo com e-mail igual ao de outro profissional já ativo, **When** ele solicita a ativação, **Then** a ativação é recusada com mensagem indicando que o e-mail deve ser único.

---

### Edge Cases

- O que acontece quando dois profissionais inativos possuem o mesmo e-mail? A unicidade do e-mail deve ser validada considerando todos os profissionais, independentemente do status, portanto dois profissionais não podem compartilhar o mesmo e-mail, mesmo que ambos estejam inativos.
- Como o sistema deve tratar a exclusão física de profissionais? A exclusão física não faz parte do escopo desta feature; a desativação é o mecanismo para retirar o profissional da agenda.
- O e-mail de um profissional inativo pode ser reutilizado para um novo profissional? Não, o e-mail deve ser único no cadastro como um todo, independentemente do status.
- Qual o comportamento esperado ao tentar atualizar um profissional inexistente? O sistema deve informar que o profissional não foi encontrado.
- Como a unicidade de e-mail trata variações como "joao@barbearia.com" e "Joao@Barbearia.com"? O sistema deve considerar esses valores como duplicados após normalização, recusando o cadastro, atualização ou ativação que gere conflito.
- O que acontece se o gestor enviar uma atualização sem informar nenhum campo? O sistema deve recusar a operação, pois não há alterações a aplicar.
- A operação de ativar/desativar deve aceitar chamadas repetidas? Sim, a operação deve ser idempotente, retornando sucesso sem modificar o profissional quando ele já estiver no status desejado.
- Um profissional recém-cadastrado deve estar ativo por padrão? Sim, o cadastro deve criar o profissional com status ativo, deixando explícita a possibilidade de desativação posterior.
- O que acontece com os agendamentos futuros já existentes de um profissional quando ele é desativado? A desativação não altera os agendamentos já criados; ela apenas impede que novos agendamentos sejam atribuídos a esse profissional.
- O sistema permite dois profissionais com o mesmo nome? Sim, desde que os e-mails sejam diferentes; o nome não possui regra de unicidade.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE permitir o cadastro de um novo profissional contendo nome, e-mail e status ativo.
- **FR-002**: O sistema DEVE exigir que o nome do profissional seja informado no cadastro.
- **FR-003**: O sistema DEVE exigir que o e-mail do profissional seja informado no cadastro.
- **FR-004**: O sistema DEVE rejeitar e-mails em formato inválido, exigindo que o e-mail possua formato válido.
- **FR-005**: O sistema DEVE garantir que o e-mail do profissional seja único em todo o cadastro, considerando uma comparação normalizada que ignore diferenças de maiúsculas/minúsculas.
- **FR-006**: O sistema DEVE permitir a consulta da lista de todos os profissionais cadastrados.
- **FR-007**: O sistema DEVE permitir a consulta dos dados de um profissional específico por meio de um identificador único.
- **FR-008**: O sistema DEVE permitir a atualização parcial dos dados de um profissional existente, aplicando as regras de validação do cadastro apenas aos campos efetivamente informados, sem permitir a alteração do status.
- **FR-009**: O sistema DEVE permitir ativar ou desativar um profissional existente por meio de uma operação dedicada, aceitando chamadas repetidas para o mesmo status sem retornar erro (idempotente).
- **FR-010**: O sistema DEVE garantir que profissionais inativos não possam receber novos agendamentos.
- **FR-011**: O sistema DEVE rejeitar nomes com mais de 120 caracteres no cadastro ou atualização de um profissional.
- **FR-012**: O sistema DEVE rejeitar e-mails com mais de 255 caracteres no cadastro ou atualização de um profissional.

### Key Entities *(include if feature involves data)*

- **Profissional**: Representa um membro da equipe da barbearia responsável pela realização dos serviços. Atributos: identificador único, nome (máximo 120 caracteres), e-mail (máximo 255 caracteres) e flag booleana `active` indicando se está ativo ou inativo. A unicidade do e-mail é avaliada de forma normalizada, ignorando diferenças de maiúsculas/minúsculas.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: O gestor da barbearia consegue cadastrar um novo profissional em menos de 1 minuto de interação.
- **SC-002**: 100% dos profissionais cadastrados respeitam as regras de validação (nome obrigatório, e-mail obrigatório, e-mail com formato válido, e-mail único).
- **SC-003**: A consulta de profissionais retorna o resultado em menos de 2 segundos para listas com até 100 profissionais cadastrados.
- **SC-004**: Profissionais inativos não são considerados disponíveis para novos agendamentos.
- **SC-005**: O gestor consegue ativar ou desativar um profissional com no máximo 2 ações.

## Clarifications

### Session 2026-09-10

- Q: A regra de unicidade do e-mail deve considerar todos os profissionais cadastrados (ativos e inativos) ou apenas os profissionais ativos? → A: O e-mail deve ser único entre todos os profissionais cadastrados, independentemente do status.
- Q: Ao desativar um profissional, o sistema deve cancelar automaticamente seus agendamentos futuros já existentes ou apenas impedir novos agendamentos? → A: A desativação deve apenas impedir novos agendamentos; agendamentos futuros já existentes permanecem inalterados.
- Q: A atualização dos dados de um profissional deve permitir alteração parcial dos campos ou exigir que todos os campos editáveis sejam informados a cada atualização? → A: A atualização deve ser parcial, permitindo enviar apenas os campos que devem ser alterados.
- Q: Qual deve ser o tamanho máximo permitido para o nome e para o e-mail de um profissional? → A: Nome até 120 caracteres e e-mail até 255 caracteres.
- Q: O nome do profissional deve ser tratado de forma normalizada para comparação e exibição, ou apenas armazenado e exibido exatamente como informado pelo gestor? → A: O nome deve ser armazenado e exibido exatamente como informado, permitindo nomes duplicados.

## Assumptions

- A feature é exclusiva para gestores da barbearia; controle de permissões detalhado será tratado em feature futura.
- O identificador único do profissional é gerado pelo sistema e não precisa ser informado pelo usuário.
- O e-mail é normalizado para validação de unicidade de forma insensível a maiúsculas/minúsculas.
- A atualização de dados é parcial: apenas os campos informados são alterados.
- A alteração de status (ativo/inativo) só pode ocorrer pela operação dedicada de ativar/desativar.
- A exclusão física de profissionais não faz parte do escopo desta feature.
- O formato válido de e-mail segue o padrão amplamente aceito para endereços de e-mail.
- Profissionais inativos continuam aparecendo na lista geral de consulta para fins de histórico e gestão, mas não estão disponíveis para novos agendamentos.
- A funcionalidade deve ser desenhada de forma a permitir, em features futuras, a associação de profissionais a serviços e a horários de atendimento.
