# Quickstart: Disponibilidade dos Profissionais

Este guia descreve como validar a feature de disponibilidade de profissionais de ponta a ponta após a implementação.

## Prerequisites

- JDK 21 instalado.
- Maven instalado.
- Docker e Docker Compose instalados.
- Banco de dados PostgreSQL e demais serviços rodando via `docker compose up`.
- Aplicação inicializada com o profile `dev`.
- Um profissional cadastrado previamente (feature 002).

## Setup

1. Suba a stack local:

   ```bash
   docker compose up -d
   ```

2. Execute as migrations do Flyway (ou inicie a aplicação, que executará automaticamente):

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. Obtenha o `professionalId` de um profissional cadastrado.

## Validation Scenarios

### Scenario 1 — Cadastrar disponibilidade válida

```bash
curl -X POST http://localhost:8080/professionals/{professionalId}/availability \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "08:00",
    "endTime": "12:00"
  }'
```

**Expected outcome**: HTTP 201 com o período criado no corpo da resposta.

### Scenario 2 — Cadastrar período consecutivo no mesmo dia

```bash
curl -X POST http://localhost:8080/professionals/{professionalId}/availability \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "12:00",
    "endTime": "17:00"
  }'
```

**Expected outcome**: HTTP 201. O período é aceito porque não há sobreposição (início exatamente no término do anterior).

### Scenario 3 — Rejeitar período com horário inválido

```bash
curl -X POST http://localhost:8080/professionals/{professionalId}/availability \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "14:00",
    "endTime": "12:00"
  }'
```

**Expected outcome**: HTTP 400 com código `INVALID_AVAILABILITY_PERIOD`.

### Scenario 4 — Rejeitar período sobreposto

```bash
curl -X POST http://localhost:8080/professionals/{professionalId}/availability \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "09:00",
    "endTime": "13:00"
  }'
```

**Expected outcome**: HTTP 400 com código `OVERLAPPING_AVAILABILITY`.

### Scenario 5 — Consultar disponibilidades

```bash
curl http://localhost:8080/professionals/{professionalId}/availability
```

**Expected outcome**: HTTP 200 com lista contendo os períodos cadastrados.

### Scenario 6 — Alterar disponibilidade

```bash
curl -X PUT http://localhost:8080/professionals/{professionalId}/availability/{availabilityId} \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 2,
    "startTime": "13:00",
    "endTime": "17:00"
  }'
```

**Expected outcome**: HTTP 200 com o período atualizado.

### Scenario 7 — Remover disponibilidade

```bash
curl -X DELETE http://localhost:8080/professionals/{professionalId}/availability/{availabilityId}
```

**Expected outcome**: HTTP 204.

### Scenario 8 — Profissional inexistente

```bash
curl -X POST http://localhost:8080/professionals/00000000-0000-0000-0000-000000000000/availability \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "startTime": "08:00",
    "endTime": "12:00"
  }'
```

**Expected outcome**: HTTP 404 com código `PROFESSIONAL_NOT_FOUND`.

## Automated Validation

Execute os testes automatizados:

```bash
./mvnw test
```

**Expected outcome**: Todos os testes unitários e de integração da feature passam.

## References

- Data model: [data-model.md](data-model.md)
- API contract: [contracts/api.md](contracts/api.md)
- Feature specification: [spec.md](spec.md)
