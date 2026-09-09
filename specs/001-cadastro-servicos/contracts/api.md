# API Contracts: Cadastro de Serviços

Base path: `/api/v1/services`

Response envelope (default):

```json
{
  "data": { ... },
  "errors": [],
  "timestamp": "2026-09-08T12:00:00Z"
}
```

---

## POST /api/v1/services

Create a new service.

### Request

```json
{
  "name": "Corte de cabelo",
  "description": "Corte social ou degradê",
  "durationMinutes": 30,
  "price": 35.00
}
```

### Response 201 Created

```json
{
  "data": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "name": "Corte de cabelo",
    "description": "Corte social ou degradê",
    "durationMinutes": 30,
    "price": 35.00,
    "active": true,
    "createdAt": "2026-09-08T12:00:00Z",
    "updatedAt": "2026-09-08T12:00:00Z"
  },
  "errors": [],
  "timestamp": "2026-09-08T12:00:00Z"
}
```

### Response 400 Bad Request

```json
{
  "data": null,
  "errors": [
    { "field": "name", "message": "Nome é obrigatório" },
    { "field": "durationMinutes", "message": "Duração deve ser maior que zero" }
  ],
  "timestamp": "2026-09-08T12:00:00Z"
}
```

### Response 409 Conflict

```json
{
  "data": null,
  "errors": [
    { "message": "Já existe um serviço ativo com este nome" }
  ],
  "timestamp": "2026-09-08T12:00:00Z"
}
```

---

## GET /api/v1/services

List all registered services (active and inactive).

### Response 200 OK

```json
{
  "data": [
    {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "name": "Corte de cabelo",
      "description": "Corte social ou degradê",
      "durationMinutes": 30,
      "price": 35.00,
      "active": true,
      "createdAt": "2026-09-08T12:00:00Z",
      "updatedAt": "2026-09-08T12:00:00Z"
    }
  ],
  "errors": [],
  "timestamp": "2026-09-08T12:00:00Z"
}
```

---

## GET /api/v1/services/{id}

Get a specific service by id.

### Response 200 OK

Same shape as POST response.

### Response 404 Not Found

```json
{
  "data": null,
  "errors": [
    { "message": "Serviço não encontrado" }
  ],
  "timestamp": "2026-09-08T12:00:00Z"
}
```

---

## PATCH /api/v1/services/{id}

Partially update a service. Only provided fields are changed. `active` cannot be changed through this endpoint.

### Request

```json
{
  "price": 40.00
}
```

### Response 200 OK

Updated service object.

### Response 400 Bad Request

Returned when a provided field is invalid or no fields are provided.

### Response 404 Not Found

Returned when the service does not exist.

### Response 409 Conflict

Returned when the new name conflicts with another active service.

---

## POST /api/v1/services/{id}/activate

Activate a service.

### Response 200 OK

Activated service object.

### Response 404 Not Found

Returned when the service does not exist.

### Response 409 Conflict

Returned when another active service already has the same normalized name.

---

## POST /api/v1/services/{id}/deactivate

Deactivate a service.

### Response 200 OK

Deactivated service object.

### Response 404 Not Found

Returned when the service does not exist.

---

## Field Rules Summary

| Field | Create | Update | Activate/Deactivate |
|-------|--------|--------|---------------------|
| name | required, unique among active (normalized) | optional, unique among active (normalized) | not allowed |
| description | optional | optional | not allowed |
| durationMinutes | required, > 0 | optional, > 0 | not allowed |
| price | required, >= 0 | optional, >= 0 | not allowed |
| active | read-only (default true) | not allowed | changed by operation |
