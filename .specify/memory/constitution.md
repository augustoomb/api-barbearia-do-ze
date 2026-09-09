<!--
Sync Impact Report
- Version change: new → 1.0.0
- Modified principles: N/A (initial constitution)
- Added sections: Core Principles (8 principles), Stack Tecnológica e Configurações, Workflow de Desenvolvimento, Governance
- Removed sections: N/A
- Follow-up TODOs: N/A
-->

# Barbearia do Zé API Constitution

## Idioma e Comunicação

1. **Idioma Principal:** Todos os artefatos gerados pelo Spec Kit (incluindo `spec.md`, `plan.md`, `tasks.md`, comentarios, informaçoes de promp, relatórios de análise, histórias de usuário e critérios de aceitação) DEVEM ser escritos em **Português do Brasil (pt-BR)**.
2. **Código e Comentários:** Elementos de código (nomes de variáveis, funções, classes e arquivos) e seus comentários técnicos devem ser mantidos em **Inglês** por padrão de mercado.

## Core Principles

### I. Arquitetura em Camadas
Toda funcionalidade da API deve ser organizada em camadas bem definidas: Controller, Service, Repository e Domain/Model. DTOs devem ser utilizados na fronteira da API para representar os contratos de entrada e saída, mantendo esses contratos desacoplados das entidades e modelos internos do domínio.
Cada camada possui responsabilidade única e não pode realizar o trabalho de outra camada.
Controllers tratam apenas de HTTP/validação de entrada; Services contêm a lógica de negócio; Repositories acessam dados.
A camada de domínio deve ser independente de frameworks, quando viável, para facilitar testes e evolução.

**Rationale**: A separação em camadas reduz o acoplamento, melhora a testabilidade e torna o código previsível para novos desenvolvedores.

### II. Stack Tecnológica Padronizada
O projeto usa Java 21 e Spring Boot como base. Todas as dependências devem ser justificadas antes de serem adicionadas.
As tecnologias obrigatórias são: Spring WebMvc, Spring Data JPA, Spring Validation, Spring AMQP, Spring Mail, PostgreSQL, Flyway, H2 (apenas para testes/cache em memória), Lombok, Docker e Docker Compose.
A troca de tecnologia da stack base só é permitida mediante revisão e atualização desta constituição.

**Rationale**: Uma stack padronizada reduz a complexidade operacional, facilita onboarding e garante previsibilidade de comportamento em produção.

### III. Qualidade e Testes (NON-NEGOTIABLE)
Toda alteração de código deve vir acompanhada de testes automatizados. Testes unitários usam JUnit 5 e Mockito.
Testes de integração verificam ponta a ponta os controllers, repositories, listeners RabbitMQ e envio de e-mail.
A cobertura de testes deve ser mantida em nível adequado, mas o foco está na qualidade e assertividade dos testes, não apenas na métrica.
Código sem testes não pode ser mergeado na branch principal.

**Rationale**: Testes são a principal salvaguarda contra regressões e documentam o comportamento esperado do sistema.

### IV. Banco de Dados e Migrations
O banco de dados principal é o PostgreSQL. Todas as alterações de schema devem ser aplicadas via Flyway, nunca manualmente.
Cada migration deve ser reversível ou acompanhada de instrução de rollback no repositório.
O H2 em memória é permitido apenas para testes automatizados ou cache temporário local; não pode substituir o PostgreSQL em ambientes de staging/produção.
Índices e constraints devem ser criados via migration e documentados quando necessário.

**Rationale**: Migrations versionadas garantem consistência entre ambientes e rastreabilidade de mudanças no schema.

### V. Mensageria e Cache
RabbitMQ é a ferramenta padrão para comunicação assíncrona entre serviços e processamento de eventos de domínio.
Todas as filas, exchanges e bindings devem ser declarados via configuração de código (Spring AMQP) e documentados.
Mensagens devem ter payload serializado em JSON, com identificador de correlação (correlation-id) e tratamento de erros com dead-letter exchange.
O cache em memória com H2 deve ser usado de forma consciente, com TTL e invalidação definidos, e nunca como fonte de verdade.

**Rationale**: Mensageria bem estruturada desacopla processos, aumenta a resiliência e permite escalabilidade horizontal.

### VI. Observabilidade e Monitoramento
A API deve expor métricas via Spring Boot Actuator no formato do Prometheus.
O dashboard padrão de observabilidade utiliza Grafana com datasource no Prometheus.
Logs devem ser estruturados, com níveis adequados (INFO, WARN, ERROR) e identificador de requisição (trace-id/correlation-id).
Alertas devem ser configurados para erros críticos, indisponibilidade de serviços dependentes (PostgreSQL, RabbitMQ) e latência anormal.

**Rationale**: Observabilidade permite detectar e diagnosticar problemas em produção antes que impactem os clientes da barbearia.

### VII. Containerização e Deploy
A aplicação e todos os serviços dependentes (PostgreSQL, RabbitMQ, Grafana, Prometheus) devem ser executáveis via Docker Compose.
Imagens Docker devem ser construídas a partir de Dockerfile multi-stage, usando Java 21 como runtime.
O ambiente de desenvolvimento local deve refletir, na medida do possível, a configuração de produção.
Segredos e configurações sensíveis não podem ser commitados; devem ser injetados via variáveis de ambiente ou secrets.

**Rationale**: Containerização padroniza a execução da aplicação em qualquer ambiente e simplifica o deploy contínuo.

### VIII. Comunicação e Notificações
O envio de e-mails utiliza Spring Mail e deve ser realizado de forma assíncrona, preferencialmente via RabbitMQ.
Templates de e-mail devem ser versionados e testados.
Toda notificação deve registrar status de envio (enviado, falha, pendente) e permitir retry em caso de erro.

**Rationale**: Notificações confiáveis são essenciais para lembretes de agendamento e comunicação com clientes da barbearia.

## Stack Tecnológica e Configurações

### Linguagem e Framework
- Java 21 (LTS)
- Spring Boot 4.x
- Maven como build tool

### Camada Web e API
- Spring WebMvc
- Spring Validation (Bean Validation/Jakarta Validation)
- DTOs com validação explícita
- Tratamento global de exceções com `@ControllerAdvice`
- Respostas padronizadas (ex.: `{ "data": ..., "errors": [...], "timestamp": ... }`)

### Persistência
- Spring Data JPA
- PostgreSQL (banco principal)
- H2 (testes automatizados e cache local em memória)
- Flyway (migrations versionadas)

### Mensageria
- RabbitMQ
- Spring AMQP
- Dead Letter Exchange (DLX) para falhas
- Correlation-id em todas as mensagens

### Notificações
- Spring Mail
- Templates de e-mail (Thymeleaf ou Mustache, a definir conforme necessidade)

### Observabilidade
- Spring Boot Actuator
- Micrometer + Prometheus
- Grafana para dashboards
- Logs estruturados (JSON em produção, quando possível)

### Qualidade
- JUnit 5
- Mockito
- Testes de integração com `@SpringBootTest` e Testcontainers quando necessário
- Maven Surefire/Failsafe

### Produtividade
- Lombok (uso consciente, preferir registros/records quando apropriado)
- MapStruct para conversão DTO/Entity (recomendado)

### DevOps
- Docker
- Docker Compose
- Dockerfile multi-stage
- Variáveis de ambiente para configuração por profile

## Roadmap para o projeto
- O arquivo ./docs/Roadmap.md será usado como base para as features do projeto

## Workflow de Desenvolvimento

1. **Branches**: use branches por feature/bugfix a partir da branch principal. Nomeie de forma descritiva (ex.: `feature/agendamento`, `fix/validacao-email`).
2. **Commits**: mensagens devem descrever o porquê da mudança, não apenas o que foi alterado.
3. **Pull Requests**: todo código deve passar por revisão antes do merge. PRs devem incluir descrição clara, testes e evidências de execução local.
4. **Quality Gates**: antes do merge, a pipeline/build deve executar testes unitários e de integração, verificar estilo de código e gerar relatório de cobertura.
5. **Local Development**: o desenvolvedor deve conseguir subir toda a stack com `docker compose up` e executar a aplicação via Maven.
6. **Profiles**: usar profiles do Spring para separar configurações de `dev`, `test` e `prod`.
7. **Documentação**: endpoints devem ser documentados com OpenAPI/SpringDoc; decisões arquiteturais relevantes devem ser registradas no repositório.

## Governance

Esta constituição é o documento de governança técnica do projeto e deve ser respeitada por todos os contribuidores.

- **Amendments**: mudanças nesta constituição devem ser propostas via PR, discutidas e aprovadas antes de serem aplicadas.
- **Versioning**: a constituição segue SemVer. MAJOR para mudanças incompatíveis, MINOR para novos princípios/seções e PATCH para clarificações.
- **Compliance**: toda PR deve indicar se há impacto nesta constituição. Mudanças que contrariem princípios existentes devem atualizar a constituição ou serem rejeitadas.
- **Review**: a constituição deve ser revisada a cada ciclo relevante do projeto ou quando houver mudança significativa de stack.
- **Guidance**: para decisões do dia a dia que não sejam cobertas por princípios, consulte a documentação do projeto, a equipe técnica e, se necessário, proponha uma emenda.

**Version**: 1.0.0 | **Ratified**: 2026-08-31 | **Last Amended**: 2026-08-31
