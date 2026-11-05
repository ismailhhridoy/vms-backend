# VMS Backend (Java Spring Boot)

Production-oriented Java Spring Boot 3 REST API for the Vehicle Management System.

---

## 🛠 Tech Stack & Architecture

- **Java Version**: 21 LTS
- **Framework**: Spring Boot 3.3.4
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL (Production & Dev) / H2 (Automated Tests)
- **Architecture**: Modular Monolith / Clean Architecture ready
  - `common/`: Cross-cutting concerns (CORS, centralized exception handling, unified `ApiResponse` format)
  - `modules/`: Self-contained business modules (reserved for features)

---

## ⚙️ Configuration & Profiles

- **`application.yml`**: Base configuration, port `8080`, context path `/api/v1`, CORS allowed origins.
- **`application-dev.yml`**: Development settings connecting to local PostgreSQL (`localhost:5432/vms_db`).
- **`application-prod.yml`**: Production settings driven by environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
- **`application-test.yml`**: In-memory H2 database for isolated testing.

---

## 🚀 Running the Service

### Prerequisites
- JDK 21
- Maven (or IDE Maven integration)
- Running PostgreSQL container (`docker compose up -d` at workspace root)

### Build and Run
```bash
# Run unit & integration tests
mvn clean test

# Run local development server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Server will be accessible at: `http://localhost:8080/api/v1`

