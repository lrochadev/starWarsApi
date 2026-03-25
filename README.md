[![CircleCI](https://circleci.com/gh/lrochadev/starWarsApi/tree/master.svg?style=shield)](https://circleci.com/gh/lrochadev/starWarsApi/tree/master)

# Star Wars API

REST API that manages Star Wars planets, enriched with movie appearance data from the public [SWAPI](https://swapi.dev).

## Tech Stack

- **Java 21**
- **Spring Boot 4**
- **MongoDB 8**
- **Maven**
- **Docker**
- **Resilience4j** — Circuit Breaker
- **Caffeine** — In-memory cache
- **WireMock** — HTTP mocking in tests
- **k6** — Stress testing

## Prerequisites

- Java 21
- Docker (with Compose plugin)

## Build & Run

**Full stack via Docker Compose (no pre-built JAR needed — multi-stage build):**
```bash
docker compose up -d
```

**Local development (requires MongoDB on port 27017):**
```bash
mvn spring-boot:run
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/planets?page=0&size=20` | List planets (paginated) |
| `POST` | `/api/planets` | Create a planet |
| `GET` | `/api/planets/{id}` | Find planet by ID |
| `GET` | `/api/planets/name/{name}` | Find planet by name (case-insensitive) |
| `DELETE` | `/api/planets/{id}` | Delete a planet |
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/metrics` | Metrics |

**Example — create a planet:**
```bash
curl -X POST http://localhost:8080/api/planets \
  -H "Content-Type: application/json" \
  -d '{"name": "Tatooine", "climate": "Arid", "terrain": "Desert"}'
```

**Example — list with pagination:**
```bash
curl "http://localhost:8080/api/planets?page=0&size=10"
```

## Swagger UI

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Architecture

### Request flow

```
POST /api/planets
  PlanetResource → PlanetServiceImpl.save() → PlanetRepository (MongoDB)
                   ↳ persists immediately with quantityOfApparitionInMovies=null

GET /api/planets (or by ID / by name)
  PlanetResource → PlanetServiceImpl.findAll(Pageable)
                 → enrichAndSaveIfNeeded(planet)
                   ↳ if quantityOfApparitionInMovies == null:
                       SwapiService.consultSwAPI()          ← Caffeine cache (TTL 1h)
                         ↳ CircuitBreaker "swapi"           ← Resilience4j
                           ↳ RestClient → swapi.dev
                       planetRepository.save(enriched)
```

### Resilience pattern — SWAPI decoupling

Planet creation **never** blocks on SWAPI availability. The `quantityOfApparitionInMovies` field is enriched lazily on first read:

1. `save()` persists the planet immediately with `quantityOfApparitionInMovies = null`
2. Read operations (`findById`, `findAll`, `findByName`) call SWAPI only if the field is `null`
3. Results are cached by planet name (Caffeine, TTL 1h, max 500 entries)
4. If SWAPI is down, the **Circuit Breaker** opens after ≥5 calls with ≥50% failure rate — subsequent reads fall back to `quantityOfApparitionInMovies = 0` without returning a 500 error
5. Circuit waits 30s before attempting recovery (half-open with 3 probe calls)

### Key layers

| Layer | Location | Responsibility |
|---|---|---|
| REST | `resources/PlanetResource` | HTTP routing, validation, pagination |
| Service | `services/PlanetServiceImpl` | Orchestration, lazy enrichment, circuit breaker fallback |
| External API | `services/SwapiService` | SWAPI HTTP call with `@Cacheable` + Circuit Breaker |
| Repository | `repository/PlanetRepository` | MongoDB via Spring Data |
| DTO/Mapper | `dto/`, `mappers/` | API contract, MapStruct compile-time mapping |
| Configuration | `configuration/ApplicationConfiguration` | RestClient, Caffeine, Circuit Breaker bean, JsonMapper |
| Retry/HTTP | `infrastructure/RetryHandlerConfiguration` | Retry on 429 / 5xx only (not 4xx) |
| Exceptions | `exception/StarWarsApiExceptionHandler` | Global `@RestControllerAdvice` |

## Tests

Tests use [TestContainers](https://testcontainers.com) (real MongoDB) — no mocks for the database layer:

```bash
mvn test
```

| Test class | Scope | Strategy |
|---|---|---|
| `PlanetRepositoryTests` | Repository layer | `@SpringBootTest` + real MongoDB (Testcontainers) |
| `PlanetResourceTests` | REST layer | MockMvc + Mockito (mocked service) |
| `SwapiServiceTests` | SwapiService | `@SpringBootTest` + WireMock (mocked HTTP server) |

## Stress Testing (k6)

Requires k6 installed and the application running:

```bash
# Run with default target (localhost:8080)
k6 run k6/stress-test.js

# Run against a custom host
k6 run -e BASE_URL=http://host:8080 k6/stress-test.js
```

**Scenario:** gradual ramp-up → 2 spikes to 200 concurrent users → ramp-down (~4 min total).

**Thresholds:** p95 < 500ms, error rate < 5%.

**Benchmark (local, single node):** 325k requests · 1,412 req/s · p95 = 2ms · error rate = 0.14%.

Report saved to `k6/stress-test-report.json` after each run.

## HTTP Client Configuration

All values configurable via `application.properties` under `resttemplate.pool.*`:

| Property | Default | Description |
|---|---|---|
| `connection-request-timeout` | 1000ms | Max wait to acquire a connection from the pool |
| `connection-timeout` | 5000ms | TCP connect timeout |
| `socket-timeout` | 1000ms | Response read timeout |
| `max-connections` | 20 | Total connection pool size |
| `max-per-route` | 6 | Max connections per host |
| `validate-after-inactivity` | 30s | Connection validation interval |
| `retry-count` | 3 | Max retry attempts (429 / 5xx only) |
| `retry-sleep-time-MS` | 20ms | Delay between retries |

## Infrastructure

- **MongoDB:** `spring.mongodb.uri=mongodb://localhost:27017/starwars_db` (local) or `mongodb://mongodb/starwars_db` (Docker via env `SPRING_MONGODB_URI`)
- **Docker Compose:** multi-stage Dockerfile builds and runs the app + MongoDB; health check on `/actuator/health`
- **CI:** GitHub Actions — Amazon Corretto 21, `mvn -B package --fail-at-end`, publishes JUnit test report as artifact

## Spring Boot 4 Notes

- **MongoDB URI:** Spring Boot 4 uses `spring.mongodb.uri` (not `spring.data.mongodb.uri` from SB3)
- **RestClient:** replaces `RestTemplate` (maintenance mode in Spring Framework 7)
- **Timeouts:** all timeout values in `RestClient`/HttpComponents are in **milliseconds** — use `Timeout.ofMilliseconds()`, not `ofSeconds()`
