# Getting Started

## Prerrequisitos

- Docker + Docker Compose
- (Opcional, solo si vas a correr la app fuera de Docker) JDK 25 — el wrapper de Gradle (`./gradlew`) ya está incluido, no hace falta instalar Gradle.

## Levantar todo con Docker Compose

```bash
docker-compose up --build
```

Esto construye la imagen de la app (`deployment/Dockerfile`, multi-stage: compila con `gradlew :app-service:bootJar` sobre `eclipse-temurin:25-jdk-alpine` y corre con usuario no-root) y levanta 4 contenedores. `app` espera a que `postgres`, `redis` y `localstack` estén healthy antes de arrancar.

Para bajar todo: `docker-compose down` (agregá `-v` si además querés borrar los volúmenes de Postgres/LocalStack).

## Qué hace cada servicio

### `postgres` (puerto 5432)
Persistencia principal. Al arrancar, la app ejecuta `applications/app-service/src/main/resources/schema.sql`, que crea la tabla `users` (`id`, `id_req_res`, `email`, `first_name`, `last_name`). Es la fuente de verdad para `GET /api/v1/users`, `GET /api/v1/users/{id}` y el fallback de la búsqueda por nombre.

### `redis` (puerto 6379)
Cache-aside delante de Postgres, usado solo por `GET /api/v1/users/search`. TTL configurable vía `adapters.redis.ttl-seconds` en `application.yaml` (default 600s = 10 min). Si hay cache hit, la app no toca Postgres.

### `localstack` (puerto 4566)
Emula AWS localmente (`SERVICES: sqs,dynamodb`), para no depender de una cuenta real de AWS en desarrollo. Al arrancar corre `deployment/localstack/init-aws.sh`, que crea:
- Cola SQS `sample`
- Tabla DynamoDB `users` (partition key `id`, tipo String, on-demand)

Rol dentro del flujo: cuando `POST /api/v1/users/{id}` crea/sincroniza un usuario, la app publica un evento a la cola `sample`; el módulo `sqs-listener` lo consume y replica el usuario en DynamoDB.

### `app` (puerto 8080)
La API Spring WebFlux. Las variables de entorno en `docker-compose.yml` mapean 1:1 a grupos de `application.yaml` (`ADAPTERS_R2DBC_*`, `SPRING_DATA_REDIS_*`, `AWS_DYNAMODB_ENDPOINT`, etc.), sobreescribiendo los defaults (`localhost`) para apuntar a los hostnames de los otros contenedores dentro de la red `reto-webflux-net`.

## Correr la app sin reconstruir el contenedor (dev loop más rápido)

Los defaults de `application.yaml` ya apuntan a `localhost`, así que alcanza con levantar solo la infraestructura y correr la app con Gradle directo:

```bash
docker-compose up -d postgres redis localstack
./gradlew :app-service:bootRun
```

Esto evita reconstruir la imagen Docker en cada cambio.

## Probar los endpoints

Importá [`Reto_Nequi.postman_collection.json`](Reto_Nequi.postman_collection.json) (raíz del proyecto) en Postman — trae las 4 rutas con ejemplos armados contra `localhost:8080`.

O con `curl`:
```bash
curl http://localhost:8080/api/v1/users
curl "http://localhost:8080/api/v1/users/search?firstName=George&lastName=Edwards"
```

## Salud de la app

`GET http://localhost:8080/actuator/health` expone también el estado del circuit breaker (`reqresGetUser`) que protege la llamada a la API externa reqres.in. `GET .../actuator/prometheus` expone métricas en formato Prometheus.
