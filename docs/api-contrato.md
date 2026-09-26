# Contrato da API

Status: `[OK]` implementado (referência) · `[TODO]` planejado — implemente seguindo
[`como-adicionar-um-endpoint.md`](como-adicionar-um-endpoint.md).

## Autenticação

| Método | Rota | Perfil | Status |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Público | `[OK]` |
| `POST` | `/api/v1/auth/register` | Público | `[OK]` |

### Login

```
POST /api/v1/auth/login
{ "email": "a@b.com", "senha": "12345678" }

200 OK
{ "token": "<jwt>", "id": 1, "nome": "Ana", "email": "a@b.com", "role": "CYCLIST" }
```

### Registro de ciclista

```
POST /api/v1/auth/register
{ "nome": "Ana", "email": "a@b.com", "senha": "12345678", "role": "CYCLIST" }
```

### Registro de parceiro

```
POST /api/v1/auth/register
{
  "nome": "Padaria Pão Quente", "email": "contato@pao.com", "senha": "12345678",
  "role": "PARTNER", "nomeFantasia": "Pão Quente", "cnpj": "12.345.678/0001-90",
  "latitude": -27.59, "longitude": -48.55
}
```

## Pontos de Interesse (POIs)

| Método | Rota | Perfil | Status |
|---|---|---|---|
| `GET` | `/api/v1/pois` | Público | `[OK]` |

```
GET /api/v1/pois?latitude=-27.59&longitude=-48.55&raio=1000

200 OK
[ { "id": 1, "tipo": "BICICLETARIO", "descricao": "...",
    "latitude": -27.59, "longitude": -48.55, "capacidade": 20 } ]
```

## Trajetos (Gamificação)

| Método | Rota | Perfil | Status |
|---|---|---|---|
| `POST` | `/api/v1/trajetos` | Ciclista | `[TODO]` (RF08–RF11) |
| `GET`  | `/api/v1/trajetos/mine` | Ciclista | `[TODO]` |

## Recompensas e Cupons

| Método | Rota | Perfil | Status |
|---|---|---|---|
| `GET`  | `/api/v1/recompensas` | Público/Autenticado | `[TODO]` (RF12) |
| `POST` | `/api/v1/recompensas/{id}/resgatar` | Ciclista | `[TODO]` (RF13) |
| `GET`  | `/api/v1/cupons/meus` | Ciclista | `[TODO]` |
| `POST` | `/api/v1/cupons/validar` | Parceiro | `[TODO]` (RF14) |

## Perfil / Administração

| Método | Rota | Perfil | Status |
|---|---|---|---|
| `GET`  | `/api/v1/usuarios/me` | Autenticado | `[TODO]` (RF03) |
| `GET`  | `/api/v1/usuarios/me/extrato` | Ciclista | `[TODO]` (RF03) |
| `POST` | `/api/v1/alertas-infraestrutura` | Ciclista | `[TODO]` (RF07) |

## Códigos HTTP

| Código | Significado |
|---|---|
| `200` | Sucesso |
| `201` | Recurso criado |
| `400` | Payload inválido |
| `401` | Não autenticado / token inválido |
| `403` | Perfil sem permissão |
| `404` | Recurso não encontrado |
| `422` | Regra de negócio (saldo insuficiente, estoque esgotado) |
| `500` | Erro interno |

## Erro padronizado

```json
{ "timestamp": "...", "status": 422, "error": "Unprocessable Entity", "message": "..." }
```
