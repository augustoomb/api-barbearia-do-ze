# Contratos da API: Cadastro de Serviços

Base path: `/api/v1/services`

Envelope padrão de resposta:

```json
{
  "data": { ... },
  "errors": [],
  "timestamp": "2026-09-08T12:00:00Z"
}
```

---

## POST /api/v1/services

Cria um novo serviço.

### Requisição

```json
{
  "name": "Corte de cabelo",
  "description": "Corte social ou degradê",
  "durationMinutes": 30,
  "price": 35.00
}
```

### Resposta 201 Created

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

### Resposta 400 Bad Request

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

### Resposta 409 Conflict

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

Lista todos os serviços cadastrados (ativos e inativos).

### Resposta 200 OK

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

Consulta um serviço específico pelo id.

### Resposta 200 OK

Mesmo formato da resposta do POST.

### Resposta 404 Not Found

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

Atualiza parcialmente um serviço. Apenas os campos enviados são alterados. O campo `active` não pode ser alterado por este endpoint.

### Requisição

```json
{
  "price": 40.00
}
```

### Resposta 200 OK

Objeto do serviço atualizado.

### Resposta 400 Bad Request

Retornada quando um campo fornecido é inválido ou quando nenhum campo é fornecido.

### Resposta 404 Not Found

Retornada quando o serviço não existe.

### Resposta 409 Conflict

Retornada quando o novo nome conflita com outro serviço ativo.

---

## POST /api/v1/services/{id}/activate

Ativa um serviço.

### Resposta 200 OK

Objeto do serviço ativado.

### Resposta 404 Not Found

Retornada quando o serviço não existe.

### Resposta 409 Conflict

Retornada quando outro serviço ativo já possui o mesmo nome normalizado.

---

## POST /api/v1/services/{id}/deactivate

Desativa um serviço.

### Resposta 200 OK

Objeto do serviço desativado.

### Resposta 404 Not Found

Retornada quando o serviço não existe.

---

## Resumo das Regras por Campo

| Campo | Criação | Atualização | Ativação/Desativação |
|-------|---------|-------------|----------------------|
| name | obrigatório, único entre ativos (normalizado) | opcional, único entre ativos (normalizado) | não permitido |
| description | opcional | opcional | não permitido |
| durationMinutes | obrigatório, > 0 | opcional, > 0 | não permitido |
| price | obrigatório, >= 0 | opcional, >= 0 | não permitido |
| active | somente leitura (default true) | não permitido | alterado pela operação |
