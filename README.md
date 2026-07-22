# Reto Webflux — Nequi

API reactiva de gestión de usuarios construida con Spring WebFlux (Java 25), siguiendo Clean Architecture. Sincroniza usuarios desde un proveedor externo (reqres.in), los persiste en PostgreSQL, cachea búsquedas por nombre en Redis y replica en DynamoDB vía eventos por SQS.

## Servicios que levanta la app

El `docker-compose.yml` levanta 4 servicios:

| Servicio | Imagen | Puerto | Rol |
|---|---|---|---|
| `app` | build local (`deployment/Dockerfile`) | 8080 | La API. Depende de que los 3 siguientes estén healthy. |
| `postgres` | postgres:16-alpine | 5432 | Persistencia principal de usuarios (tabla `users`). |
| `redis` | redis:7-alpine | 6379 | Cache-aside para la búsqueda por nombre. |
| `localstack` | localstack/localstack:4.4.0 | 4566 | Emula SQS y DynamoDB para desarrollo local. |

Detalle de qué hace cada uno y cómo levantarlos individualmente → [getting-started.md](getting-started.md).

## Rutas

Base URL: `http://localhost:8080`

| Método | Path | Qué hace | Devuelve |
|---|---|---|---|
| `POST` | `/api/v1/users/{id}` | Busca el usuario `{id}` en reqres.in; si no existe localmente lo persiste en Postgres y publica un evento (SQS → DynamoDB) | `200` con el usuario creado/sincronizado |
| `GET` | `/api/v1/users/search?firstName=&lastName=` | Busca por nombre + apellido exacto (case-insensitive). Cache-aside: primero Redis, si no está, cae a Postgres y cachea el resultado | `200` con lista de usuarios (vacía si no hay match) |
| `GET` | `/api/v1/users/{id}` | Busca un usuario ya persistido por su id interno | `200` con el usuario, `404` si no existe |
| `GET` | `/api/v1/users` | Lista todos los usuarios persistidos | `200` con la lista completa |

Todos los errores responden con el mismo shape:
```json
{ "code": "400", "message": "...", "param": "..." }
```

Colección de Postman lista para importar: [`Reto_Nequi.postman_collection.json`](Reto_Nequi.postman_collection.json) (incluye las 4 rutas con ejemplos).

## Cómo levantar la app

```bash
docker-compose up --build
```

Esperá a que los healthchecks pasen; la API queda expuesta en `http://localhost:8080`.

```bash
docker-compose down          # apagar
docker-compose logs -f app   # ver logs de la app
```

Guía detallada (paso a paso, rol de cada servicio, cómo correr sin reconstruir el contenedor) → [getting-started.md](getting-started.md).

## Arquitectura y patrones

Clean Architecture (plugin de Bancolombia) en módulos Gradle:

- `domain/model` — entidades y contratos (gateways) del negocio, sin dependencias externas.
- `domain/usecase` — casos de uso, orquestan los gateways.
- `infrastructure/driven-adapters/*` — implementaciones concretas de los gateways: `r2dbc-postgresql` (persistencia), `redis` (cache), `dynamo-db` (réplica), `sqs-sender` (publicación de eventos), `rest-consumer` (cliente reactivo a reqres.in con circuit breaker).
- `infrastructure/entry-points/*` — puntos de entrada: `reactive-web` (rutas HTTP funcionales) y `sqs-listener` (consumidor de eventos).
- `applications/app-service` — ensambla todos los módulos e inicia la app.

Patrones puntuales: cache-aside (búsqueda por nombre), circuit breaker (Resilience4j, protege la llamada a reqres.in), y un helper genérico reutilizable para adapters de persistencia clave-valor (Redis/DynamoDB).
