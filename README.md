# Enrich+ Workforce Management Platform

Enrich+ is a Java/Spring Boot microservices repository that models workforce lifecycle and staffing operations used in enterprise delivery organizations.  
This codebase includes candidate onboarding, candidate profile enrichment (CV upload path + in-app resume builder path), demand intake, and allocation orchestration.

---

## 1) What problem this project solves

Enterprise staffing teams need to continuously balance:

- Candidate/employee availability
- Project demand (skill-wise slot requirements)
- Timely allocation decisions with traceability

Enrich+ handles this through three collaborating services:

- **staffing-service**: candidate lifecycle + profile data + availability
- **demand-service**: demand creation and fulfillment tracking
- **allocation-service**: orchestration engine that matches demand with available talent

---

## 2) High-level architecture

### Services and ports

- **demand-service**: `8081`
- **staffing-service**: `8082`
- **allocation-service**: `8083`

### Architecture style

- Microservices using synchronous REST communication
- Shared domain library (`common`) for entities and DTOs
- JPA repositories for data access (DAO style)
- Shared PostgreSQL database

### Runtime flow

1. Candidate is onboarded in staffing service.
2. Candidate profile is enriched using one of two methods:
   - CV upload metadata path
   - Resume builder path
3. Demand is created in demand service.
4. Allocation cycle is triggered in allocation service.
5. Allocation service calls staffing + demand services and persists audit records.

### Diagrammatic structure (full-fledged enterprise view)

```text
┌──────────────────────────────────────────────────────────────────────────┐
│                              Experience Layer                            │
├───────────────────────────────┬──────────────────────────────────────────┤
│ Candidate Portal              │ Staffing Partner Portal                 │
│ - Onboard profile             │ - Raise/track demands                   │
│ - Upload CV                   │ - Search available talent               │
│ - Build resume in app         │ - Trigger allocation cycle              │
└───────────────┬───────────────┴───────────────────────────┬──────────────┘
                │                                           │
                └──────────────────────┬────────────────────┘
                                       │
                           API Gateway / BFF (future)
                                       │
     ┌─────────────────────────────────┼──────────────────────────────────┐
     │                                 │                                  │
┌────▼──────────────────┐   ┌──────────▼──────────┐            ┌──────────▼──────────┐
│ staffing-service      │   │ demand-service      │            │ allocation-service   │
│ :8082                 │   │ :8081               │            │ :8083                │
│ - candidate lifecycle │   │ - demand intake     │            │ - orchestration      │
│ - profile enrich flow │   │ - fulfillment       │            │ - sync clients       │
└────┬──────────────────┘   └──────────┬──────────┘            └──────────┬──────────┘
     │                                 │                                  │
     └─────────────────────────────────┴──────────────────────────────────┘
                                       │
                              Shared PostgreSQL
                                       │
                           Reporting / Audit (future)
```

---

## 3) Repository structure

```text
.
├── common/              # Shared entities + DTOs
├── demand-service/      # Demand APIs
├── staffing-service/    # Employee/candidate lifecycle + profile APIs
├── allocation-service/  # Allocation orchestration APIs
├── docker-compose.yml   # Local full stack (PostgreSQL + microservices)
└── pom.xml              # Parent multi-module Maven build
```

---

## 4) Technology stack

- **Java 17**
- **Spring Boot 3**
- **Spring Web (REST APIs)**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven multi-module build**
- **JUnit via spring-boot-starter-test**

---

## 5) Domain model

### Employee (`employees`)

Primary fields:

- `id`
- `name`
- `email` (unique)
- `primarySkill`
- `status` (`AVAILABLE`, `ALLOCATED`, `OFFBOARDED`)
- `skillsCsv` (expanded skills list)
- `yearsOfExperience`
- `professionalSummary`
- `cvUrl`
- `profileSource` (`BASIC_ONBOARDING`, `CV_UPLOAD`, `RESUME_BUILDER`)

### TalentDemand (`talent_demands`)

- `id`
- `projectCode`
- `requiredSkill`
- `requestedCount`
- `fulfilledCount`

### AllocationRecord (`allocations`)

- `id`
- `employeeId`
- `demandId`
- `allocationStatus`

### AllocationDecision (DTO response)

- `demandId`
- `employeeId`
- `message`

---

## 6) API reference

## Staffing Service (`/api/staffing`)

### 1) Onboard candidate
`POST /api/staffing/onboard`

```json
{
  "name": "Anika Rao",
  "email": "anika@enrichplus.com",
  "primarySkill": "Java"
}
```

### 2) Candidate uploads CV metadata (enterprise path 1)
`PUT /api/staffing/{id}/cv`

```json
{
  "cvUrl": "https://blob.example.com/cv/anika-rao-v3.pdf",
  "primarySkill": "Java",
  "skillsCsv": "Java,Spring Boot,Kafka,PostgreSQL",
  "yearsOfExperience": 5,
  "professionalSummary": "Backend developer focused on scalable microservices."
}
```

### 3) Candidate uses Resume Builder (enterprise path 2)
`PUT /api/staffing/{id}/resume-builder`

```json
{
  "primarySkill": "Java",
  "skillsCsv": "Java,Spring Boot,REST,SQL",
  "yearsOfExperience": 5,
  "professionalSummary": "Built and supported enterprise workforce applications."
}
```

### 4) Get candidate profile
`GET /api/staffing/{id}/profile`

### 5) Availability & lifecycle actions

- `GET /api/staffing/available?skill=Java`
- `PATCH /api/staffing/{id}/allocate`
- `PATCH /api/staffing/{id}/offboard`

---

## Demand Service (`/api/demands`)

### 1) Create demand
`POST /api/demands`

```json
{
  "projectCode": "PRJ-DELTA",
  "requiredSkill": "Java",
  "requestedCount": 2
}
```

### 2) List demands
`GET /api/demands`

### 3) Fulfill one slot
`PATCH /api/demands/{id}/fulfill`

---

## Allocation Service (`/api/allocations`)

### Run allocation cycle
`POST /api/allocations/run`

Behavior:

1. Fetch all demands.
2. For each demand, compute open slots (`requested - fulfilled`).
3. Query available candidates by required skill from staffing service.
4. Allocate matching candidates.
5. Increment fulfilled count in demand service.
6. Save allocation audit records.

---

## 7) End-to-end sequence (whiteboard ready)

1. Staffing partner onboards candidate.
2. Candidate enriches profile via CV upload or resume builder.
3. Demand manager creates skill demand.
4. Staffing partner triggers allocation run.
5. Allocation service returns allocation decisions and persists audit trail.

---

## 8) Setup and run locally

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker + Docker Compose

### Start full stack with Docker (Postgres + all microservices)

```bash
docker compose up -d --build
```

Verify containers:

```bash
docker compose ps
```

### If Docker run fails

Use these commands to inspect build/runtime errors quickly:

```bash
docker compose build --no-cache
docker compose up -d
docker compose ps
docker compose logs demand-service staffing-service allocation-service --tail=200
```

Typical causes:
- Docker Desktop not running / Docker CLI unavailable.
- Corporate proxy/network blocking Maven Central during image build.
- Port conflict on `5432`, `8081`, `8082`, or `8083`.

### Optional: run services from IDE/terminal instead of Docker

If you prefer running services locally (without containers), start only Postgres from Docker and run each service with Maven:

```bash
docker compose up -d postgres
mvn -pl demand-service spring-boot:run
mvn -pl staffing-service spring-boot:run
mvn -pl allocation-service spring-boot:run
```

### Build all modules

```bash
mvn clean install
```

### Quick smoke run (sample)

1. Onboard employee:

```bash
curl -X POST http://localhost:8082/api/staffing/onboard \
  -H "Content-Type: application/json" \
  -d '{"name":"Anika Rao","email":"anika@enrichplus.com","primarySkill":"Java"}'
```

2. Add candidate profile by CV flow:

```bash
curl -X PUT http://localhost:8082/api/staffing/1/cv \
  -H "Content-Type: application/json" \
  -d '{"cvUrl":"https://blob.example.com/cv/anika.pdf","primarySkill":"Java","skillsCsv":"Java,Spring Boot,PostgreSQL","yearsOfExperience":5,"professionalSummary":"Backend engineer"}'
```

3. Create demand:

```bash
curl -X POST http://localhost:8081/api/demands \
  -H "Content-Type: application/json" \
  -d '{"projectCode":"PRJ-DELTA","requiredSkill":"Java","requestedCount":1}'
```

4. Run allocation cycle:

```bash
curl -X POST http://localhost:8083/api/allocations/run
```

---

## 9) Configuration

All services use local PostgreSQL by default:

- URL: `jdbc:postgresql://localhost:5432/enrichplus`
- Username: `enrich_user`
- Password: `enrich_pass`

Allocation service client URLs:

- `clients.demand-service-url=http://localhost:8081`
- `clients.staffing-service-url=http://localhost:8082`

---

## 10) Typical interview explanations

### Basic-level

- It is a Spring Boot microservices project for workforce lifecycle and staffing.
- Staffing service keeps candidate data and availability.
- Demand service captures required skills.
- Allocation service matches supply to demand.

### Intermediate-level

- Allocation service is an orchestrator that calls two services synchronously.
- Candidate profile can be built through CV upload metadata or resume builder payload.
- JPA entities are centralized in the `common` module.

### Expert-level

- Trade-off: shared DB simplifies early development but couples services.
- Current synchronous workflow is simple but can face partial-failure/race conditions at scale.
- Recommended evolution:
  - Outbox + event-driven updates
  - Idempotency keys for allocation runs
  - Optimistic locking/versioning for concurrent updates
  - API contracts separated from persistence entities
  - Stronger validation + audit history for profile changes

### Trade-offs used in this implementation

1. **Shared DB vs DB per service**
   - Used: shared DB for delivery speed and lower operational complexity.
   - Trade-off: faster implementation, but tighter coupling and weaker service autonomy.

2. **Synchronous REST orchestration vs event-driven messaging**
   - Used: synchronous REST for easier debugging and deterministic request/response behavior.
   - Trade-off: simple flow, but risk of cascading latency/failures under heavy load.

3. **Single `skillsCsv` field vs normalized skill model**
   - Used: CSV field for lightweight profile enrichment.
   - Trade-off: quick to ship, but weaker query flexibility and data integrity.

4. **Entity reuse as API payload vs strict request/response contracts**
   - Used: direct entity-oriented payloads for faster development.
   - Trade-off: fewer classes, but increased coupling between DB model and API contracts.

### Advantages of this architecture

- Clear domain separation (staffing, demand, allocation).
- Easy onboarding for developers due to straightforward module boundaries.
- End-to-end flow is easy to demo and reason about for business users.
- Allocation audit data supports traceability/reporting.
- Extensible design for candidate profile enrichment (CV + resume builder paths).

### Performance improvement strategy (what to discuss in interviews)

Current improvements:
- Reduced unnecessary logic in services by keeping orchestration centralized in allocation service.
- Query-by-skill and status avoids scanning unrelated employees for allocation.

Planned improvements:
- Add indexes on `employees(primary_skill, status)` and `talent_demands(required_skill)`.
- Add pagination for demand and candidate fetching.
- Use batching for large allocation cycles.
- Add caching for frequently queried available skills.
- Add resilience patterns (timeouts, retries, circuit breaker) around inter-service calls.
- Move to async event-driven allocation updates for high throughput scenarios.

---

## 11) Known limitations and roadmap

### Current limitations

- CV upload API currently stores CV metadata URL, not binary file streaming.
- No authentication/authorization layer yet.
- No centralized observability or distributed tracing.
- No advanced ranking/scoring engine for candidate matching.

### Suggested next steps

- Add secure multipart upload with object storage integration.
- Add role-based access control.
- Add retries/circuit breakers for inter-service calls.
- Introduce event-driven async architecture for allocation state transitions.
- Add richer candidate skill model (normalized table instead of CSV).

---

## 12) Testing

Run all tests:

```bash
mvn test
```

Run module tests:

```bash
mvn -pl staffing-service test
mvn -pl demand-service test
mvn -pl allocation-service test
```

---

## 13) Developer notes

- This repository is intentionally designed as a clear learning + interview-friendly microservice baseline.
- The new candidate profile APIs align with the enterprise Enrich platform behavior where candidates can either:
  - Upload CV details
  - Build resume inside the application

---

## 7) Postman assets

A ready-to-import Postman setup has been added under `postman/`:

- `postman/Enrich.postman_collection.json` - entity-wise API collection with folders for:
  - `Employees` (staffing-service endpoints)
  - `Demands` (demand-service endpoints)
  - `Allocations` (allocation-service endpoint)
- `postman/Enrich.local.postman_environment.json` - local environment (ports 8081/8082/8083)
- `postman/Enrich.sample_data.json` - sample data payloads for quick testing and runner usage

### Import order

1. Import environment: `Enrich.local.postman_environment.json`
2. Import collection: `Enrich.postman_collection.json`
3. (Optional) Use `Enrich.sample_data.json` with Collection Runner / Newman.

