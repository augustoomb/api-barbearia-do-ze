# Feature Specification: Disponibilidade dos Profissionais

**Feature Branch**: `004-disponibilidade-profissionais`

**Created**: 2026-09-29

**Status**: Draft

**Input**: User description: "Feature 3 — Disponibilidade dos Profissionais

Permitir cadastrar e gerenciar a disponibilidade semanal de cada profissional.

Requisitos:
- Uma disponibilidade pertence a um profissional existente.
- Cada período deve informar dia da semana, horário de início e horário de término.
- Um profissional pode possuir mais de um período no mesmo dia.
- Deve ser possível consultar, alterar e remover períodos de disponibilidade.
- Não permitir períodos com horário de término igual ou anterior ao horário de início.
- Não permitir períodos conflitantes para o mesmo profissional e dia.
- Não permitir cadastrar disponibilidade para profissional inexistente.

Escopo:
- Esta feature trata apenas do cadastro e gerenciamento da disponibilidade semanal.
- A disponibilidade será utilizada por features futuras para determinar horários disponíveis para agendamento.
- Não implementar agendamentos nem cálculo de horários livres para clientes nesta feature."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Cadastrar disponibilidade de um profissional (Priority: P1)

Como gestor da barbearia, quero informar os dias e horários em que um profissional está disponível para atendimento, para que a barbearia possa organizar futuros agendamentos com base nessa disponibilidade.

**Why this priority**: Sem o cadastro da disponibilidade, não é possível registrar quando cada profissional pode atender clientes. Esta é a base para toda a funcionalidade de agendamento futura.

**Independent Test**: Pode ser testada cadastrando um ou mais períodos de disponibilidade para um profissional existente e verificando que os dados são salvos corretamente.

**Acceptance Scenarios**:

1. **Given** que existe um profissional cadastrado, **When** o gestor cadastra um período de disponibilidade informando dia da semana, horário de início e horário de término válidos, **Then** o sistema salva o período e o associa ao profissional.
2. **Given** que existe um profissional cadastrado, **When** o gestor cadastra múltiplos períodos no mesmo dia da semana sem que haja sobreposição de horários, **Then** o sistema salva todos os períodos corretamente.

---

### User Story 2 - Consultar disponibilidade de um profissional (Priority: P2)

Como gestor da barbearia, quero visualizar os períodos de disponibilidade cadastrados de um profissional, para poder conferir e organizar os horários de atendimento.

**Why this priority**: A consulta permite auditar e acompanhar os horários cadastrados, sendo essencial para o gerenciamento operacional, embora dependa do cadastro prévio.

**Independent Test**: Pode ser testada consultando a disponibilidade de um profissional que possui períodos cadastrados e verificando que a lista retornada está completa e correta.

**Acceptance Scenarios**:

1. **Given** que um profissional possui períodos de disponibilidade cadastrados, **When** o gestor consulta a disponibilidade desse profissional, **Then** o sistema retorna todos os períodos cadastrados com dia da semana, horário de início e horário de término.
2. **Given** que um profissional não possui períodos de disponibilidade cadastrados, **When** o gestor consulta a disponibilidade desse profissional, **Then** o sistema retorna uma lista vazia.

---

### User Story 3 - Alterar e remover disponibilidade de um profissional (Priority: P2)

Como gestor da barbearia, quero alterar ou remover períodos de disponibilidade já cadastrados, para corrigir erros ou adaptar os horários do profissional às necessidades da barbearia.

**Why this priority**: A manutenção dos períodos é necessária para manter a disponibilidade atualizada, mas pode ser realizada de forma independente das demais funcionalidades.

**Independent Test**: Pode ser testada alterando ou removendo um período existente e verificando que a operação é refletida corretamente na consulta subsequente.

**Acceptance Scenarios**:

1. **Given** que existe um período de disponibilidade cadastrado, **When** o gestor altera o horário de início, o horário de término ou o dia da semana, **Then** o sistema atualiza o período desde que as novas informações sejam válidas, não conflitem com outros períodos do mesmo profissional e o profissional vinculado permaneça inalterado.
2. **Given** que existe um período de disponibilidade cadastrado, **When** o gestor remove o período, **Then** o sistema exclui o período e ele não é mais retornado em consultas futuras.

---

### Edge Cases

- O que acontece quando o horário de término é igual ou anterior ao horário de início?
- Como o sistema trata a tentativa de cadastrar disponibilidade para um profissional inexistente?
- Como o sistema trata a tentativa de criar ou atualizar um período que se sobrepõe a outro período do mesmo profissional no mesmo dia?
- O que acontece quando se tenta alterar ou remover um período de disponibilidade inexistente?
- Como o sistema trata a inclusão de múltiplos períodos consecutivos no mesmo dia (por exemplo, 08:00–12:00 e 12:00–17:00)? *(Resolvido: períodos consecutivos são permitidos.)*

## Clarifications

### Session 2026-09-29

- **Q**: Dois períodos de disponibilidade do mesmo profissional no mesmo dia devem ser considerados conflitantes quando um começa exatamente no horário em que o outro termina? → **A**: Períodos consecutivos são permitidos; a sobreposição só existe quando um período começa antes do outro terminar.
- **Q**: Qual deve ser o comportamento do sistema ao tentar alterar ou remover um período de disponibilidade que não existe? → **A**: O sistema deve retornar erro informando que o período não foi encontrado.
- **Q**: Ao alterar um período de disponibilidade, deve ser possível transferi-lo para outro profissional? → **A**: Não; o profissional vinculado a um período existente não pode ser alterado. Para vincular o período a outro profissional, deve-se remover o período original e criar um novo.
- **Q**: A restrição de que apenas gestores ou administradores podem cadastrar, alterar ou remover disponibilidades deve ser implementada nesta feature ou deixada para uma feature futura de autorização? → **A**: Deixar para feature futura; nesta feature, não aplicar controle de autorização específico sobre disponibilidades.
- **Q**: Os critérios de sucesso SC-001 (tempo para cadastrar) e SC-004 (tempo de resposta da consulta) devem ser mantidos, simplificados ou removidos da especificação? → **A**: Simplificar: remover as métricas de tempo específicas e manter critérios focados em integridade, validação e consistência dos dados.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE permitir cadastrar um período de disponibilidade vinculado a um profissional existente.
- **FR-002**: Cada período de disponibilidade DEVE conter o dia da semana, o horário de início e o horário de término.
- **FR-003**: O sistema DEVE permitir que um profissional possua mais de um período de disponibilidade no mesmo dia da semana.
- **FR-004**: O sistema DEVE permitir consultar todos os períodos de disponibilidade de um profissional.
- **FR-005**: O sistema DEVE permitir alterar um período de disponibilidade cadastrado.
- **FR-006**: O sistema DEVE permitir remover um período de disponibilidade cadastrado.
- **FR-007**: O sistema NÃO DEVE permitir o cadastro ou alteração de um período cujo horário de término seja igual ou anterior ao horário de início.
- **FR-008**: O sistema NÃO DEVE permitir o cadastro ou alteração de um período que se sobreponha a outro período existente do mesmo profissional no mesmo dia da semana. Períodos consecutivos — aqueles em que um começa exatamente no horário em que o outro termina — NÃO devem ser considerados sobreposição.
- **FR-009**: O sistema NÃO DEVE permitir o cadastro de disponibilidade para um profissional inexistente.
- **FR-010**: O sistema NÃO DEVE permitir alterar um período de disponibilidade inexistente; deve retornar erro informando que o período não foi encontrado.
- **FR-011**: O sistema NÃO DEVE permitir remover um período de disponibilidade inexistente; deve retornar erro informando que o período não foi encontrado.
- **FR-012**: O sistema NÃO DEVE permitir alterar o profissional vinculado a um período de disponibilidade existente.

### Key Entities *(include if feature involves data)*

- **Profissional**: Representa o colaborador da barbearia para o qual a disponibilidade será cadastrada. Já deve existir previamente no sistema.
- **Disponibilidade (Período de Disponibilidade)**: Representa um intervalo de tempo em um dia da semana em que o profissional está disponível para atendimento. Possui dia da semana, horário de início e horário de término.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% dos períodos cadastrados devem estar corretamente vinculados a um profissional existente.
- **SC-002**: 100% dos períodos cadastrados devem passar pelas validações de horário e conflito antes de serem persistidos.
- **SC-003**: Nenhum período inválido (horário de término anterior ou igual ao início, sobreposto a outro do mesmo profissional ou vinculado a profissional inexistente) é persistido no sistema.
- **SC-004**: A consulta de disponibilidade de um profissional retorna todos os períodos cadastrados de forma completa e consistente.
- **SC-005**: A consulta de disponibilidade retorna os períodos ordenados por dia da semana e horário de início crescentes.

## Assumptions

- O cadastro de profissionais já foi implementado em feature anterior e é requisito para esta funcionalidade.
- O horário de funcionamento da barbearia não limita a disponibilidade dos profissionais nesta feature; qualquer horário válido pode ser cadastrado.
- A disponibilidade é semanal e se repete todas as semanas, sem variação por data específica.
- O controle de autorização (restringir o gerenciamento de disponibilidades a gestores/administradores) será tratado em feature futura. Nesta feature, o foco está apenas nas regras de cadastro, consulta, alteração e remoção dos períodos.
- A remoção de um profissional não necessita de tratamento específico nesta feature; a integridade referencial será garantida pela regra de negócio existente.
