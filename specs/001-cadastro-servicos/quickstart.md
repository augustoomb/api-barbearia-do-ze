# Quickstart: Cadastro de Serviços

## Pré-requisitos

- Docker e Docker Compose instalados
- Java 21 e Maven instalados
- Repositório clonado e branch `001-cadastro-serviços` ativa

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

### 1. Criar um serviço

```bash
curl -X POST http://localhost:8080/api/v1/services \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Corte de cabelo",
    "description": "Corte social ou degradê",
    "durationMinutes": 30,
    "price": 35.00
  }'
```

**Esperado**: HTTP 201 com o serviço criado e `active: true`.

### 2. Listar serviços

```bash
curl http://localhost:8080/api/v1/services
```

**Esperado**: HTTP 200 com um array contendo o serviço criado.

### 3. Atualizar o preço do serviço

```bash
curl -X PATCH http://localhost:8080/api/v1/services/{id} \
  -H "Content-Type: application/json" \
  -d '{"price": 40.00}'
```

**Esperado**: HTTP 200 com o preço atualizado e os demais campos inalterados.

### 4. Tentar nome ativo duplicado

```bash
curl -X POST http://localhost:8080/api/v1/services \
  -H "Content-Type: application/json" \
  -d '{
    "name": "corte de cabelo",
    "durationMinutes": 30,
    "price": 35.00
  }'
```

**Esperado**: HTTP 409 indicando que já existe um serviço ativo com o mesmo nome normalizado.

### 5. Desativar serviço

```bash
curl -X POST http://localhost:8080/api/v1/services/{id}/deactivate
```

**Esperado**: HTTP 200 com `active: false`.

### 6. Idempotência de reativação

```bash
curl -X POST http://localhost:8080/api/v1/services/{id}/activate
curl -X POST http://localhost:8080/api/v1/services/{id}/activate
```

**Esperado**: Ambas as requisições retornam HTTP 200 e o serviço permanece ativo.

## Execução dos Testes

```bash
./mvnw test
```

**Esperado**: Todos os testes unitários e de integração passam.

## Observações

- Substitua `{id}` pelo UUID real do serviço retornado na criação.
- Veja [data-model.md](../data-model.md) para detalhes da entidade e [contracts/api.md](./api.md) para os contratos completos da API.
