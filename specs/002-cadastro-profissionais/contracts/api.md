# Contratos da API: Cadastro de Profissionais

Base path: `/api/v1/professionals`

Envelope padrão de resposta:

```json
{
  "data": { ... },
  "errors": [],
  "timestamp": "2026-09-11T12:00:00Z"
}
```

---

## POST /api/v1/professionals

Cria um novo profissional.

### Requisição

```json
{
  "name": "João Silva",
  "email": "joao.silva@barbearia.com"
}
```

### Resposta 201 Created

```json
{
  "data": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "name": "João Silva",
    "email": "joao.silva@barbearia.com",
    "active": true,
    "createdAt": "2026-09-11T12:00:00Z",
    "updatedAt": "2026-09-11T12:00:00Z"
  },
  "errors": [],
  "timestamp": "2026-09-11T12:00:00Z"
}
```

### Resposta 400 Bad Request

```json
{
  "data": null,
  "errors": [
    { "field": "name", "message": "Nome é obrigatório" },
    { "field": "email", "message": "E-mail é obrigatório" }
  ],
  "timestamp": "2026-09-11T12:00:00Z"
}
```

### Resposta 409 Conflict

```json
{
  "data": null,
  "errors": [
    { "message": "Já existe um profissional com este e-mail" }
  ],
  "timestamp": "2026-09-11T12:00:00Z"
}
```

---

## GET /api/v1/professionals

Lista todos os profissionais cadastrados (ativos e inativos).

### Resposta 200 OK

```json
{
  "data": [
    {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "name": "João Silva",
      "email": "joao.silva@barbearia.com",
      "active": true,
      "createdAt": "2026-09-11T12:00:00Z",
      "updatedAt": "2026-09-11T12:00:00Z"
    }
  ],
  "errors": [],
  "timestamp": "2026-09-11T12:00:00Z"
}
```

---

## GET /api/v1/professionals/{id}

Consulta um profissional específico pelo id.

### Resposta 200 OK

Mesmo formato da resposta do POST.

### Resposta 404 Not Found

```json
{
  "data": null,
  "errors": [
    { "message": "Profissional não encontrado" }
  ],
  "timestamp": "2026-09-11T12:00:00Z"
}
```

---

## PATCH /api/v1/professionals/{id}

Atualiza parcialmente um profissional. Apenas os campos enviados são alterados. O campo `active` não pode ser alterado por este endpoint.

### Requisição

```json
{
  "email": "joao.silva.novo@barbearia.com"
}
```

### Resposta 200 OK

Objeto do profissional atualizado.

### Resposta 400 Bad Request

Retornada quando um campo fornecido é inválido ou quando nenhum campo é fornecido.

### Resposta 404 Not Found

Retornada quando o profissional não existe.

### Resposta 409 Conflict

Retornada quando o novo e-mail conflita com outro profissional cadastrado.

---

## POST /api/v1/professionals/{id}/activate

Ativa um profissional.

### Resposta 200 OK

Objeto do profissional ativado.

### Resposta 404 Not Found

Retornada quando o profissional não existe.

### Resposta 409 Conflict

Retornada quando outro profissional já possui o mesmo e-mail normalizado.

---

## POST /api/v1/professionals/{id}/deactivate

Desativa um profissional.

### Resposta 200 OK

Objeto do profissional desativado.

### Resposta 404 Not Found

Retornada quando o profissional não existe.

---

## Resumo das Regras por Campo

| Campo | Criação | Atualização | Ativação/Desativação |
|-------|---------|-------------|----------------------|
| name | obrigatório, máximo 120 caracteres | opcional, máximo 120 caracteres | não permitido |
| email | obrigatório, formato válido, único (normalizado), máximo 255 caracteres | opcional, formato válido, único (normalizado), máximo 255 caracteres | não permitido |
| active | somente leitura (default true) | não permitido | alterado pela operação |
