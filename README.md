[![CircleCI](https://circleci.com/gh/lrochadev/starWarsApi/tree/master.svg?style=shield)](https://circleci.com/gh/lrochadev/starWarsApi/tree/master)

# Star Wars API

REST API that manages Star Wars planets, enriched with movie appearance data from the public [SWAPI](https://swapi.dev).

## Tech Stack

- **Java 21**
- **Spring Boot 4**
- **MongoDB 8**
- **Maven**
- **Docker**

## Prerequisites

- Java 21
- Docker (with Compose plugin)

## Build & Run

**1. Build the JAR:**
```bash
mvn clean package -DskipTests
```

**2. Start the full stack (API + MongoDB):**
```bash
docker compose up -d
```

**3. Local development (requires a local MongoDB on port 27017):**
```bash
mvn spring-boot:run
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/planets` | List all planets |
| `POST` | `/api/planets` | Create a planet |
| `GET` | `/api/planets/{id}` | Find planet by ID |
| `GET` | `/api/planets/name/{name}` | Find planet by name |
| `DELETE` | `/api/planets/{id}` | Delete a planet |

**Example — create a planet:**
```bash
curl -X POST http://localhost:8080/api/planets \
  -H "Content-Type: application/json" \
  -d '{"name": "Tatooine", "climate": "Arid", "terrain": "Dessert"}'
```

## Swagger UI

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Tests

Tests use [TestContainers](https://testcontainers.com) — no external MongoDB required:
```bash
mvn test
```
