# SeismoScore — Earthquake Monitoring System

Full-stack earthquake monitoring application built for the Codeit internship challenge.
The backend gets earthquake data from the USGS GeoJSON feed and serves it
through a REST API; the frontend visualizes it on a map with charts.

- **Backend:** Spring Boot 4 (Java 25), PostgreSQL, Spring Data JPA
- **Frontend:** React 19 + Vite, Leaflet, Recharts, Axios, Bootstrap
- **Data source:** [USGS earthquake feed](https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary)

## Repository layout

```
earthquakes/
├── backend/    Spring Boot REST API
└── frontend/   React + Vite single-page app
```

## Prerequisites

- Java 25
- Maven (the repo ships with the Maven wrapper, so the system-wide install is optional)
- Node.js 20+ and npm
- PostgreSQL 14+ (local install, Docker, or a managed service such as Neon)

## Database configuration

The backend uses PostgreSQL with `spring.jpa.hibernate.ddl-auto=validate`, so
the schema must exist before the application starts.

1. Create a database (example: `earthquakes`).
2. Apply the schema in `backend/src/main/resources/db/schema.sql`:
   ```bash
   psql "$DB_URL" -f backend/src/main/resources/db/schema.sql
   ```
3. Add information in `backend/src/main/resources/application-local.properties`:
   ```
   DB_URL=jdbc:postgresql://<host>:5432/earthquakes
   DB_USERNAME=<user>
   DB_PASSWORD=<password>
   ```

The `local` Spring profile is active by default
(`spring.profiles.active=local` in `application.properties`).

## Running the backend

From the `backend/` directory:

```bash
./mvnw.cmd spring-boot:run
```

or if you are using IntelliJ, start the application from the main class.

The app should start on `http://localhost:8080`.

### Endpoints

| Method | Path                         | Description                                	    |
|--------|------------------------------|---------------------------------------------------|
| GET    | `/api/earthquakes`           | List all earthquakes (filters: `after`, `minMag`) |
| GET    | `/api/earthquakes/{id}`      | Fetch one earthquake                              |
| DELETE | `/api/earthquakes/{id}`      | Delete an earthquake                              |
| POST   | `/api/earthquakes/refresh`   | Pull the latest USGS feed into the DB             |

### API documentation (Swagger / OpenAPI)

Once the backend is running:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Running tests

```bash
./mvnw.cmd test
```

## Running the frontend

From the `frontend/` directory:

```bash
npm install
npm run dev
```

The dev server should start on `http://localhost:5173` (default Vite port) and
talks to the backend at `http://localhost:8080`.

## Assumptions

- Earthquake data is refreshed on demand via `POST /api/earthquakes/refresh`, there is no scheduled job.
- Required filtering for earthquakes with magnitude greater than 2.0 was not specified if it needs to be from an api filter, or filtered before storing in the database; api filtering implemented
- The USGS `all_day.geojson` feed is used as the default source; other feeds can be swapped in via `earthquakes.usgs.feed` in `application.properties`.
- `external_id` from USGS is treated as the stable deduplication key, so repeated refreshes upsert rather than create duplicates.
- All timestamps are stored and returned in `Instant` per latest guidelines.
- CORS is open to all origins under `/api/**` since the frontend can run on a different port in development.

## Optional improvements implemented

- **OpenAPI / Swagger UI** — springdoc integration with tags, operation
  descriptions, parameter examples, and typed error responses.
- **Global exception handling** — `GlobalExceptionHandler` maps domain and
  framework exceptions to a consistent `ErrorResponse` body with appropriate
  HTTP status codes (400, 404, 502, 500).
- **Filtering** — `after` and `minMag` query parameters on the list endpoint.
- **Separation of concerns** — service interface + implementation split,
  record-based DTOs, dedicated USGS client and DTO package.
- **Test coverage** — unit tests for the service and controller layers plus
  tests for the external USGS client.
- **Typed external API layer** — USGS payloads are parsed into dedicated
  DTOs (`UsgsResponseDto`, `UsgsFeatureDto`, …) rather than generic maps.

## Further improvements
- **Scheduled job** — the earthquake data can be refreshed automatically for up-to-date information
- **Filters** — more filters can be introduced for the `GET /api/earthquakes` endpoint
- **Authentication** — authentication can be introduced to secure the application
