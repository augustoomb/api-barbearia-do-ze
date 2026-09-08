# Quickstart: Cadastro de Serviços

## Prerequisites

- Docker e Docker Compose instalados
- Java 21 e Maven instalados
- Repositório clonado e branch `001-cadastro-serviços` ativa

## Setup

1. Start the infrastructure:

```bash
docker compose up -d postgres
```

2. Run the application:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Validation Scenarios

### 1. Create a service

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

**Expected**: HTTP 201 with the created service and `active: true`.

### 2. List services

```bash
curl http://localhost:8080/api/v1/services
```

**Expected**: HTTP 200 with an array containing the created service.

### 3. Update service price

```bash
curl -X PATCH http://localhost:8080/api/v1/services/{id} \
  -H "Content-Type: application/json" \
  -d '{"price": 40.00}'
```

**Expected**: HTTP 200 with updated price and other fields unchanged.

### 4. Try duplicate active name

```bash
curl -X POST http://localhost:8080/api/v1/services \
  -H "Content-Type: application/json" \
  -d '{
    "name": "corte de cabelo",
    "durationMinutes": 30,
    "price": 35.00
  }'
```

**Expected**: HTTP 409 indicating an active service with the same normalized name already exists.

### 5. Deactivate service

```bash
curl -X POST http://localhost:8080/api/v1/services/{id}/deactivate
```

**Expected**: HTTP 200 with `active: false`.

### 6. Re-activation idempotency

```bash
curl -X POST http://localhost:8080/api/v1/services/{id}/activate
curl -X POST http://localhost:8080/api/v1/services/{id}/activate
```

**Expected**: Both requests return HTTP 200 and the service remains active.

## Running Tests

```bash
./mvnw test
```

**Expected**: All unit and integration tests pass.

## Notes

- Replace `{id}` with the actual service UUID returned by creation.
- See [data-model.md](../data-model.md) for entity details and [contracts/api.md](./api.md) for full API contracts.
