# Enrich+ Workforce Management (Java Microservices)

Enrich+ is a Java/Spring Boot microservices project for workforce lifecycle and staffing operations.
It models onboarding/offboarding, talent demand intake, and allocation workflows for global delivery teams.

## Services

- **staffing-service (8082)**
  - Onboard/offboard employees
  - Query available talent by skill
  - Mark employees as allocated

- **demand-service (8081)**
  - Create project staffing demands
  - Track fulfilled slots

- **allocation-service (8083)**
  - Runs synchronous allocation cycles
  - Calls demand and staffing services with `RestTemplate`
  - Persists allocation records for reporting/auditing

All services use a **shared PostgreSQL database** and JPA repositories (DAO style).

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Maven multi-module build

## Project Structure

```
.
├── common/              # Shared entities + DTOs
├── demand-service/      # Demand forecasting/intake APIs
├── staffing-service/    # Employee lifecycle + staffing APIs
├── allocation-service/  # Allocation orchestration APIs
└── docker-compose.yml   # PostgreSQL
```

## Run

1. Start DB:
   ```bash
   docker compose up -d
   ```
2. Build all modules:
   ```bash
   mvn clean install
   ```
3. Start each service in separate terminals:
   ```bash
   mvn -pl demand-service spring-boot:run
   mvn -pl staffing-service spring-boot:run
   mvn -pl allocation-service spring-boot:run
   ```

## Sample API Flow

1. Onboard employee:
   ```http
   POST /api/staffing/onboard
   {
     "name": "Anika Rao",
     "email": "anika@enrichplus.com",
     "primarySkill": "Java"
   }
   ```
2. Create demand:
   ```http
   POST /api/demands
   {
     "projectCode": "PRJ-DELTA",
     "requiredSkill": "Java",
     "requestedCount": 2
   }
   ```
3. Run allocation cycle:
   ```http
   POST /api/allocations/run
   ```
