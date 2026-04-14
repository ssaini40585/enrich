# Go Through: Enrich+ Interview and Deep-Dive Guide

This file is a structured preparation guide for discussing Enrich+ from beginner to expert levels.

---

## 1) One-minute explanation

Enrich+ is a Spring Boot microservices workforce platform with three core services:

- **staffing-service** for candidate lifecycle and profile enrichment
- **demand-service** for staffing demand intake
- **allocation-service** for orchestration that maps supply to demand

Candidate profile enrichment supports two real enterprise-aligned flows:

1. CV upload metadata path
2. Resume builder path

---

## 2) Architecture questions you should be ready for

### Q1. Why this microservice split?
- Staffing, demand, and allocation represent separate business domains with different release/change frequencies.

### Q2. Why shared PostgreSQL?
- Faster MVP delivery, simpler setup, and easier cross-service reporting in the early phase.
- Trade-off is tighter coupling and lower service autonomy.

### Q3. Why synchronous REST calls for allocation?
- Easy to understand and debug; deterministic execution for each cycle.
- Trade-off: under scale, can create latency chains and partial failure concerns.

### Q4. Where are enterprise profile features implemented?
- Candidate profile is enriched in staffing-service using:
  - `PUT /api/staffing/{id}/cv`
  - `PUT /api/staffing/{id}/resume-builder`

### Q5. How are profile sources tracked?
- `profileSource` field stores `BASIC_ONBOARDING`, `CV_UPLOAD`, or `RESUME_BUILDER`.

---

## 3) Core interview question bank

## Basic

1. What does each service do?
2. Which database do you use and why?
3. How do you onboard and allocate an employee?
4. What is the role of the `common` module?

## Intermediate

1. How does allocation cycle work internally?
2. How do you ensure demand `fulfilledCount` stays accurate?
3. How do you prevent allocation of offboarded users?
4. How does candidate profile enrichment impact staffing decisions?

## Advanced / System Design

1. How would you scale allocation for 100k employees and 20k open demands?
2. How would you avoid race conditions in concurrent allocation runs?
3. How would you redesign for event-driven architecture?
4. How would you introduce DB-per-service gradually?
5. How would you implement full CV binary upload securely?
6. How would you implement search relevance for multi-skill matching?
7. What observability stack would you add and why?

---

## 4) Performance improvement talking points

### Already aligned in architecture
- Separation of concerns by service boundaries.
- Focused API surface per domain.
- Allocation logic centralized in orchestrator.

### Recommended technical upgrades
- DB indexing for skill/status and demand skill.
- Pagination/streaming for large candidate pools.
- Chunked allocation processing with configurable batch size.
- Idempotency key for `/api/allocations/run`.
- Retry + timeout + circuit breaker around downstream calls.
- Async event model (Kafka/outbox) for high-volume systems.
- Caching of availability snapshots for hot skills.

---

## 5) Trade-off matrix

| Decision | Why chosen now | Benefit | Trade-off | Future evolution |
|---|---|---|---|---|
| Shared DB | Simplicity and speed | Fast MVP | Coupling | DB-per-service |
| Sync REST | Easy debugging | Deterministic flows | Latency chains | Event-driven |
| skillsCsv | Rapid profile rollout | Minimal schema complexity | Hard query semantics | Normalized skill tables |
| Entity-heavy payloads | Less boilerplate | Fast coding | API/DB coupling | Dedicated contracts |

---

## 6) “How staffing partner uses this” walkthrough

1. Candidate is onboarded.
2. Candidate updates profile either by CV upload metadata or resume builder.
3. Staffing partner checks available candidates by skill.
4. Demand manager creates demand.
5. Staffing partner triggers allocation run.
6. Allocation records provide audit trail and reporting base.

---

## 7) Risks and mitigations

### Risk: partial failure during allocation
- Example: employee marked allocated, demand increment fails.
- Mitigation: saga/outbox + retry + compensating action.

### Risk: duplicate allocation under concurrent triggers
- Mitigation: optimistic locking + transactional guards + idempotency tokens.

### Risk: stale profile or skill mismatch
- Mitigation: profile versioning + periodic revalidation workflows.

---

## 8) HR + behavioral mapping (project ownership)

Questions to expect:

1. “What was your specific role in this platform?”
2. “Tell me about a production issue and how you resolved it.”
3. “How did you collaborate with staffing/business stakeholders?”
4. “What trade-off did you intentionally make and why?”
5. “If you had 3 more months, what would you improve first?”

Good answer style:
- Situation → Task → Action → Result.
- Include one reliability example and one performance example.

---

## 9) Short final pitch

“Enrich+ is an enterprise-style staffing platform where candidate profiles can be enriched via CV upload or in-app resume builder, demands are managed separately, and allocation is orchestrated through microservices with auditable outcomes. The current implementation optimizes for delivery simplicity and clear domain flow, while providing a path to scale via event-driven architecture, resilient communication patterns, and stronger data isolation.”

