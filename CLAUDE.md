# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build (skip tests)
mvn clean package -DskipTests

# Run all tests
mvn test

# Run single test class
mvn test -Dtest=PlanetResourceTests

# Run single test method
mvn test -Dtest=PlanetResourceTests#listPlanetsShouldReturnStatusCode200

# Run application locally (requires MongoDB on localhost:27017)
mvn spring-boot:run

# Full stack via Docker Compose (multi-stage build — no pre-built JAR needed)
docker compose up -d

# Run k6 stress test (requires app running)
k6 run k6/stress-test.js
```

## Architecture

Spring Boot 4 / Java 21 layered application integrating with the external [SWAPI](https://swapi.dev) to enrich planet data with movie appearance counts.

### SWAPI Enrichment — Lazy Pattern (CRITICAL)

Planet creation is **decoupled** from SWAPI. The `quantityOfApparitionInMovies` field is `null` after `save()` and populated lazily on first read:

```
save()      → persist planet with quantityOfApparitionInMovies=null (never calls SWAPI)
findById()
findAll()   → enrichAndSaveIfNeeded(planet):
findByName()     if null → SwapiService.consultSwAPI() [cached + circuit breaker]
                          → persist enriched planet
                          → return DTO
```

**Do NOT revert to calling SWAPI in `save()`** — this broke availability when SWAPI was down.

### Circuit Breaker (Resilience4j)

`SwapiService.consultSwAPI()` is wrapped by a `CircuitBreaker` bean named `"swapi"`:
- Opens after ≥5 calls with ≥50% failure rate (10-call sliding window)
- Wait 30s in open state; 3 probe calls in half-open
- Fallback in `PlanetServiceImpl.enrichAndSaveIfNeeded()`: catches `CallNotPermittedException`, sets count=0

### Cache (Caffeine)

`SwapiService.consultSwAPI()` is annotated with `@Cacheable(value = "swapi-planets", key = "#planetName.toLowerCase()")`.
- TTL: 1 hour, max 500 entries (configured via `spring.cache.caffeine.spec`)
- Cache is enabled via `@EnableCaching` on `StarWarsApiApplication`

### Pagination

`GET /api/planets` accepts `Pageable` (`?page=0&size=20`). Default page size = 20. Returns `Page<PlanetDto>` — JSON has `content`, `totalElements`, `totalPages`, etc.

**Request flow:**
```
PlanetResource (REST) → PlanetServiceImpl → PlanetRepository (MongoDB)
                                          ↘ SwapiService (lazy, cached, circuit breaker)
```

**Key layers:**
- `resources/` — REST controllers (`PlanetResource` at `/api/planets`)
- `services/` — Business logic; `PlanetServiceImpl` orchestrates lazy enrichment; `SwapiService` handles external HTTP
- `repository/` — `PlanetRepository` extends `MongoRepository<Planet, String>`
- `dto/` — `PlanetDto` (API contract), `SwapiDto`/`PropertiesDto` (external API response)
- `mappers/` — MapStruct mapper (`@Mapper(componentModel = "spring")`) between `Planet` and `PlanetDto`
- `configuration/` — `ApplicationConfiguration`: `RestClient`, `CircuitBreaker` bean, `Caffeine` cache, `JsonMapper`, `MessageSource`
- `infrastructure/` — `RetryHandlerConfiguration`: retries 429 and 5xx only (NOT 403/404)
- `exception/` — `StarWarsApiExceptionHandler` (`@RestControllerAdvice`): 400 (validation), 404 (not found), 500 (SWAPI)

## Testing Strategy

Tests use **TestContainers** (real MongoDB) — no mocks for the database layer.

| Class | Scope | Notes |
|---|---|---|
| `ContainerBase` | Shared setup | Reusable `MongoDBContainer` via `@ServiceConnection` |
| `ControllerTest` | MockMvc base | Includes `StarWarsApiExceptionHandler`; custom `JsonMapper` |
| `PlanetRepositoryTests` | Repository | `@SpringBootTest` + real MongoDB |
| `PlanetResourceTests` | REST layer | MockMvc + Mockito; `findAll()` mock must use `any(Pageable.class)` and return `Page` |
| `SwapiServiceTests` | SwapiService | `@SpringBootTest` + WireMock; `@DynamicPropertySource` overrides `starwars.api.url` |

## External API

`SwapiService` calls `https://swapi.dev/api/planets?search={name}`. Uses `RestClient` with connection pooling, retry, Caffeine cache, and Resilience4j circuit breaker.

## Known Gotchas

### Timeouts use milliseconds, not seconds
`ApplicationConfiguration` configures `RequestConfig` with `Timeout.ofMilliseconds()`. The `resttemplate.pool.*` properties are in **ms**. Using `ofSeconds()` with these values would result in timeouts of 1000+ seconds.

### Retry strategy: 429 and 5xx only
`RetryHandlerConfiguration` retries on `429 Too Many Requests` and `>= 500` status codes. Do NOT add 403/404 to the retry list — they are client errors that won't resolve on retry.

### Planet.quantityOfApparitionInMovies is Integer (nullable)
The domain model uses `Integer` (not `int`) to allow `null` as the sentinel value for "not yet enriched". MapStruct's generated `toDomain()` handles the null-check.

### developerMessage exposes only simple class name
`StarWarsApiExceptionHandler` uses `ex.getClass().getSimpleName()` — not `getName()`. Tests assert against the short name (e.g., `"PlanetNotFoundException"`).

### Locale is request-scoped
`MessageUtil` uses `LocaleContextHolder.getLocale()` — the locale follows the request's `Accept-Language` header, not a hardcoded PT-BR.

## Spring Boot 4 Notes

- **MongoDB URI property changed:** Spring Boot 4 uses `spring.mongodb.uri` (not `spring.data.mongodb.uri` from SB3)
- **RestClient replaces RestTemplate:** `RestTemplate` is in maintenance mode in Spring Framework 7
- **Docker:** multi-stage Dockerfile — `eclipse-temurin:21-jdk-alpine` for build, `eclipse-temurin:21-jre-alpine` for runtime; MongoDB URI passed via `SPRING_MONGODB_URI` env var

## API Documentation

Swagger UI: `http://localhost:8080/swagger-ui/index.html`
Health check: `http://localhost:8080/actuator/health`

## Infrastructure

- **MongoDB:** `spring.mongodb.uri=mongodb://localhost:27017/starwars_db` (local) or `mongodb://mongodb/starwars_db` (Docker)
- **Docker Compose:** Spins up `mongo:8.0` + app container with health check; multi-stage build (no pre-built JAR required)
- **CI:** GitHub Actions (`.github/workflows/maven.yml`) — Amazon Corretto 21, `mvn -B package --fail-at-end`, publishes JUnit test report
- **Stress tests:** `k6/stress-test.js` — ramp-up to 200 VUs with 2 spikes; thresholds p95<500ms, error<5%
