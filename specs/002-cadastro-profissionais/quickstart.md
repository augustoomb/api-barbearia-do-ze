# Quickstart: Cadastro de Profissionais

## Pré-requisitos

- Docker e Docker Compose instalados
- Java 21 e Maven instalados
- Repositório clonado e branch `003-cadastro-profissionais` ativa

## Setup

1. Inicie a infraestrutura:

```bash
docker compose up -d postgres
```

2. Execute a aplicação:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Cenários de Validação

### 1. Criar um profissional

```bash
curl -X POST http://localhost:8080/api/v1/professionals \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao.silva@barbearia.com"
  }'
```

**Esperado**: HTTP 201 com o profissional criado e `active: true`.

### 2. Listar profissionais

```bash
curl http://localhost:8080/api/v1/professionals
```

**Esperado**: HTTP 200 com um array contendo o profissional criado.

### 3. Atualizar o e-mail do profissional

```bash
curl -X PATCH http://localhost:8080/api/v1/professionals/{id} \
  -H "Content-Type: application/json" \
  -d '{"email": "joao.silva.novo@barbearia.com"}'
```

**Esperado**: HTTP 200 com o e-mail atualizado e os demais campos inalterados.

### 4. Tentar e-mail duplicado com variação de maiúsculas/minúsculas

```bash
curl -X POST http://localhost:8080/api/v1/professionals \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva 2",
    "email": "JOAO.SILVA@barbearia.com"
  }'
```

**Esperado**: HTTP 409 indicando que já existe um profissional com o mesmo e-mail.

### 5. Desativar profissional

```bash
curl -X POST http://localhost:8080/api/v1/professionals/{id}/deactivate
```

**Esperado**: HTTP 200 com `active: false`.

### 6. Idempotência de reativação

```bash
curl -X POST http://localhost:8080/api/v1/professionals/{id}/activate
curl -X POST http://localhost:8080/api/v1/professionals/{id}/activate
```

**Esperado**: Ambas as requisições retornam HTTP 200 e o profissional permanece ativo.

### 7. Tentar ativar profissional com e-mail duplicado

```bash
# Criar um segundo profissional inativo
curl -X POST http://localhost:8080/api/v1/professionals \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva 3",
    "email": "joao.silva3@barbearia.com"
  }'

# Desativar o segundo profissional
curl -X POST http://localhost:8080/api/v1/professionals/{id2}/deactivate

# Tentar alterar o e-mail do segundo profissional inativo para o e-mail do primeiro ativo
curl -X PATCH http://localhost:8080/api/v1/professionals/{id2} \
  -H "Content-Type: application/json" \
  -d '{"email": "joao.silva@barbearia.com"}'
```

**Esperado**: HTTP 409 indicando conflito de e-mail.

## Execução dos Testes

```bash
./mvnw test
```

**Esperado**: Todos os testes unitários e de integração passam.

## Observações

- Substitua `{id}` e `{id2}` pelos UUIDs reais dos profissionais retornados na criação.
- Veja [data-model.md](../data-model.md) para detalhes da entidade e [contracts/api.md](./api.md) para os contratos completos da API.
