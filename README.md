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
- **Spring Retry** — Async retry with exponential backoff
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
                   ↳ ApplicationEventPublisher.publishEvent(PlanetCreatedEvent)
                                                  │
                              ┌───────────────────┘ (async, non-blocking)
                              ▼
                   PlanetEnrichmentListener (@Async @EventListener)
                     → PlanetEnrichmentUpdater.enrich() (@Retryable: 3x, backoff 200ms × 2)
                       → SwapiService.consultSwAPI()    ← Caffeine cache (TTL 1h)
                         ↳ CircuitBreaker "swapi"        ← Resilience4j
                           ↳ RestClient → swapi.dev
                       → planetRepository.save(enriched) (background write)

GET /api/planets (or by ID / by name)
  PlanetResource → PlanetServiceImpl.findAll(Pageable)
                 → enrichAndSaveIfNeeded(planet)        ← FALLBACK ONLY
                   ↳ if quantityOfApparitionInMovies == null (async still pending or failed):
                       SwapiService.consultSwAPI()       ← cached
                       (no write — read path is pure)
```

### Resilience pattern — SWAPI decoupling

Planet creation **never** blocks on SWAPI availability. Enrichment runs **asynchronously after persist**, with a lazy fallback on read for defense-in-depth:

1. `save()` persists the planet immediately with `quantityOfApparitionInMovies = null`
2. A `PlanetCreatedEvent` is published; `PlanetEnrichmentListener` (`@Async @EventListener`) handles it off the request thread
3. The listener delegates to `PlanetEnrichmentUpdater.enrich()`, annotated with `@Retryable(retryFor=SWAPIException, maxAttempts=3, backoff=@Backoff(delay=200, multiplier=2.0))` — so transient SWAPI failures are retried with exponential backoff (200ms → 400ms → 800ms)
4. Results are cached by planet name (Caffeine, TTL 1h, max 500 entries)
5. If async enrichment exhausts retries or the circuit is open, `@Recover` logs and returns — the read path will lazily re-attempt enrichment on next access (without writing to MongoDB)
6. If SWAPI is down at read time, the **Circuit Breaker** (Resilience4j) opens after ≥5 calls with ≥50% failure rate — subsequent reads fall back to `quantityOfApparitionInMovies = 0` without returning a 500 error
7. Circuit waits 30s before attempting recovery (half-open with 3 probe calls)

### Why async post-save (and not sync at save / sync at read)

| Approach | Save latency | Read latency | Availability if SWAPI down |
|---|---|---|---|
| Sync at save (original) | Slow (~500ms-2s) | Fast | ❌ POST fails |
| Sync at read (lazy + write-during-read) | Fast | Slow on first read (~500ms-2s) + extra MongoDB write | ✅ |
| **Async post-save (current)** | **Fast** | **Fast** (no SWAPI call, no write) | ✅ |

### Key layers

| Layer | Location | Responsibility |
|---|---|---|
| REST | `resources/PlanetResource` | HTTP routing, validation, pagination |
| Service | `services/PlanetServiceImpl` | Save + publish event; read with lazy fallback (no write) |
| Async enrichment | `services/PlanetEnrichmentListener` | `@Async @EventListener`; catches `CallNotPermittedException` |
| Retry layer | `services/PlanetEnrichmentUpdater` | `@Retryable` SWAPI call + persist; `@Recover` on exhaustion |
| Event | `services/PlanetCreatedEvent` | Record (id, name) |
| External API | `services/SwapiService` | SWAPI HTTP call with `@Cacheable` + Circuit Breaker |
| Repository | `repository/PlanetRepository` | MongoDB via Spring Data |
| Domain | `model/Planet` | `@Indexed` on `name` for IXSCAN on `findByNameIgnoreCaseContaining` |
| DTO/Mapper | `dto/`, `mappers/` | API contract, MapStruct compile-time mapping |
| Configuration | `configuration/ApplicationConfiguration` | `RestClient` (Jackson 3 converter), Caffeine, Circuit Breaker bean, `MessageSource`; HttpClient 5 via `PoolingHttpClientConnectionManagerBuilder` + `ConnectionConfig` |
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

**Benchmark (local, single node, after async post-save + `@Retryable`):**
- 50,500 requests · 219 req/s · 100 VUs · 3m50s
- `http_req_duration`: avg 2.20ms · p95 4.07ms · max 1.32s
- `get_planet_by_id`: avg 1.76ms · p95 2.21ms
- `list_planets`: avg 3.46ms · p95 4.91ms · max 27ms
- error rate: 0%

Report saved to `k6/stress-test-report.json` after each run.

## HTTP Client Configuration

All values configurable via `application.properties` under `resttemplate.pool.*`:

| Property | Default | Description |
|---|---|---|
| `connection-request-timeout` | 1000ms | Max wait to acquire a connection from the pool |
| `connection-timeout` | 5000ms | TCP connect timeout (`ConnectionConfig`) |
| `socket-timeout` | 300ms | Response read timeout (also used as `RequestConfig.responseTimeout`) |
| `max-connections` | 20 | Total connection pool size |
| `max-per-route` | 6 | Max connections per host |
| `validate-after-inactivity` | 30s | Connection validation interval (`ConnectionConfig`) |
| `retry-count` | 1 | Max retry attempts (429 / 5xx only) |
| `retry-sleep-time-MS` | 20ms | Delay between retries |

## Infrastructure

- **MongoDB:** `spring.mongodb.uri=mongodb://localhost:27017/starwars_db` (local) or `mongodb://mongodb/starwars_db` (Docker via env `SPRING_MONGODB_URI`)
- **Docker Compose:** multi-stage Dockerfile builds and runs the app + MongoDB; health check on `/actuator/health`
- **CI:** GitHub Actions — Amazon Corretto 21, `mvn -B package --fail-at-end`, publishes JUnit test report as artifact

## Spring Boot 4 Notes

- **MongoDB URI:** Spring Boot 4 uses `spring.mongodb.uri` (not `spring.data.mongodb.uri` from SB3)
- **RestClient:** replaces `RestTemplate` (maintenance mode in Spring Framework 7)
- **Jackson 3:** SF7 deprecates `MappingJackson2HttpMessageConverter`; this project uses `JacksonJsonHttpMessageConverter` (Jackson 3 / `tools.jackson.*`) configured via `RestClient.Builder.configureMessageConverters(...)`
- **HttpClient 5:** connect timeout and validate-after-inactivity moved from `RequestConfig` / `PoolingHttpClientConnectionManager` setters to `ConnectionConfig` (built via `PoolingHttpClientConnectionManagerBuilder.create()`)
- **Timeouts:** all timeout values in `RestClient`/HttpComponents are in **milliseconds** — use `Timeout.ofMilliseconds()`, not `ofSeconds()`
