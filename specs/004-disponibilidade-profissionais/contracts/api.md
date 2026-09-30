# API Contract: Disponibilidade dos Profissionais

**Base path**: `/api/v1/professionals/{professionalId}/availability`

**Content-Type**: `application/json`

**Response envelope** (padrão do projeto):

```json
{
  "data": { ... },
  "errors": [],
  "timestamp": "2026-09-29T12:00:00Z"
}
```

---

## 1. Create Availability

Cria um novo período de disponibilidade para um profissional.

**Endpoint**: `POST /api/v1/professionals/{professionalId}/availability`

### Path Parameters

| Name | Type | Description |
|------|------|-------------|
| `professionalId` | UUID | Identificador do profissional. |

### Request Body

```json
{
  "dayOfWeek": 1,
  "startTime": "08:00",
  "endTime": "12:00"
}
```

### Request Fields

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `dayOfWeek` | Integer | `1` a `7`, required | Dia da semana. |
| `startTime` | String (ISO-8601 time) | required, format `HH:mm` | Horário de início. |
| `endTime` | String (ISO-8601 time) | required, format `HH:mm`, > `startTime` | Horário de término. |

### Responses

#### 201 Created

```json
{
  "data": {
    "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "professionalId": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22",
    "dayOfWeek": 1,
    "startTime": "08:00",
    "endTime": "12:00",
    "createdAt": "2026-09-29T12:00:00Z",
    "updatedAt": "2026-09-29T12:00:00Z"
  },
  "errors": [],
  "timestamp": "2026-09-29T12:00:00Z"
}
```

#### 400 Bad Request

- Horário de término anterior ou igual ao início.
- Período conflitante com outro período do mesmo profissional.
- Campos inválidos ou ausentes.

```json
{
  "data": null,
  "errors": [
    {
      "code": "OVERLAPPING_AVAILABILITY",
      "message": "O período informado conflita com outro período de disponibilidade do profissional."
    }
  ],
  "timestamp": "2026-09-29T12:00:00Z"
}
```

#### 404 Not Found

- Profissional não encontrado.

---

## 2. List Availabilities by Professional

Retorna todos os períodos de disponibilidade de um profissional, ordenados por dia da semana e horário de início crescentes.

**Endpoint**: `GET /api/v1/professionals/{professionalId}/availability`

### Path Parameters

| Name | Type | Description |
|------|------|-------------|
| `professionalId` | UUID | Identificador do profissional. |

### Query Parameters (optional)

| Name | Type | Description |
|------|------|-------------|
| `dayOfWeek` | Integer | Filtrar por dia da semana (`1` a `7`). |

### Responses

#### 200 OK

```json
{
  "data": [
    {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "professionalId": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22",
      "dayOfWeek": 1,
      "startTime": "08:00",
      "endTime": "12:00",
      "createdAt": "2026-09-29T12:00:00Z",
      "updatedAt": "2026-09-29T12:00:00Z"
    }
  ],
  "errors": [],
  "timestamp": "2026-09-29T12:00:00Z"
}
```

#### 404 Not Found

- Profissional não encontrado.

---

## 3. Update Availability

Altera um período de disponibilidade existente. O profissional vinculado não pode ser alterado.

**Endpoint**: `PUT /api/v1/professionals/{professionalId}/availability/{availabilityId}`

### Path Parameters

| Name | Type | Description |
|------|------|-------------|
| `professionalId` | UUID | Identificador do profissional. |
| `availabilityId` | UUID | Identificador do período de disponibilidade. |

### Request Body

```json
{
  "dayOfWeek": 2,
  "startTime": "13:00",
  "endTime": "17:00"
}
```

### Request Fields

Mesmos campos e restrições do endpoint de criação.

### Responses

#### 200 OK

Retorna o período atualizado.

#### 400 Bad Request

- Horário inválido.
- Período conflitante.
- Tentativa de alterar `professionalId` (se enviado no payload).

#### 404 Not Found

- Profissional não encontrado.
- Período de disponibilidade não encontrado.

---

## 4. Delete Availability

Remove um período de disponibilidade.

**Endpoint**: `DELETE /api/v1/professionals/{professionalId}/availability/{availabilityId}`

### Path Parameters

| Name | Type | Description |
|------|------|-------------|
| `professionalId` | UUID | Identificador do profissional. |
| `availabilityId` | UUID | Identificador do período de disponibilidade. |

### Responses

#### 204 No Content

Período removido com sucesso.

#### 404 Not Found

- Profissional não encontrado.
- Período de disponibilidade não encontrado.

---

## Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `PROFESSIONAL_NOT_FOUND` | 404 | Profissional informado não existe. |
| `AVAILABILITY_NOT_FOUND` | 404 | Período de disponibilidade informado não existe. |
| `INVALID_AVAILABILITY_PERIOD` | 400 | Horário de término é igual ou anterior ao horário de início. |
| `OVERLAPPING_AVAILABILITY` | 400 | Período conflita com outro período do mesmo profissional no mesmo dia. |
| `VALIDATION_ERROR` | 400 | Campos inválidos ou ausentes no payload. |
