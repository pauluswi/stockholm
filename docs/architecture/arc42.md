# arc42 Architecture Documentation

# Stockholm

## Event-Driven SEPA Payment Orchestrator with AI-Assisted Transaction Anomaly Detection and DORA-Inspired Operational Resilience

Version 1.0

Author: Slamet Widodo

---

# 1. Introduction and Goals

## 1.1 Purpose

Stockholm demonstrates how a modern European payment platform can orchestrate SEPA Instant Credit Transfer transactions using ISO 20022 while adopting cloud-native architecture, event-driven communication, operational resilience, and AI-assisted transaction monitoring.

Rather than implementing a complete banking platform, Stockholm focuses on software architecture and engineering practices typically expected in modern financial institutions.

The project simulates payment orchestration, settlement, reporting, anomaly detection, and operational resilience using lightweight mock services.

---

## 1.2 Business Goals

| Goal                      | Description                                                       |
| ------------------------- | ----------------------------------------------------------------- |
| ISO 20022                 | Demonstrate European payment messaging                            |
| Payment Orchestration     | End-to-end SEPA payment lifecycle                                 |
| Event-Driven Architecture | Loose coupling using Kafka                                        |
| Operational Resilience    | Demonstrate DORA-inspired engineering practices                   |
| AI Integration            | Explainable transaction anomaly detection                         |
| Observability             | Full transaction traceability                                     |
| Auditability              | Immutable payment history                                         |
| Portfolio                 | Demonstrate software architecture capability for European banking |

---

## 1.3 Stakeholders

| Stakeholder            | Concern                 |
| ---------------------- | ----------------------- |
| Solution Architect     | Overall architecture    |
| Software Engineers     | Implementation guidance |
| Recruiters             | Architecture capability |
| Technical Interviewers | Engineering decisions   |
| Banking Architects     | ISO 20022 understanding |

---

# 2. Architecture Constraints

## Technical

* Java 21
* Spring Boot
* Maven
* Kafka
* PostgreSQL
* Redis
* Docker
* REST APIs

Optional

* Kubernetes
* Keycloak
* Prometheus
* Grafana

---

## Business Constraints

* No connection to real banking infrastructure
* Local execution
* Lightweight mock services
* Educational purposes
* Open-source friendly

---

# 3. System Scope and Context

## Business Context

```
Client

    |

Payment API

    |

Stockholm

    |

+------------------------+
| Mock Clearing Network  |
| Mock Settlement        |
| Mock AI Risk Engine    |
| Mock Reporting         |
+------------------------+
```

External systems are intentionally mocked to keep the project lightweight while preserving realistic architecture.

---

## Technical Context

```
REST

↓

Payment Orchestrator

↓

Kafka

↓

Settlement
Ledger
Reporting
AI
Monitoring
```

---

# 4. Solution Strategy

The architecture follows several key principles.

* Event-driven communication
* Domain-driven design
* Loose coupling
* Asynchronous processing
* Immutable events
* Idempotent operations
* Cloud-native deployment
* Observability first
* Security by default
* Resilience by design

The system emphasizes maintainability and extensibility over implementation complexity.

---

# 5. Building Block View

## Level 1

```
Stockholm Platform

├── Payment Orchestrator
├── Settlement Service
├── Ledger Service
├── Reporting Service
├── AI Anomaly Detection
├── Resilience Monitor
└── Backoffice API
```

---

## Level 2

### Payment Orchestrator

Responsibilities

* Validate requests
* Generate pacs.008
* Publish Kafka events
* Track payment status
* Correlation IDs

---

### Settlement Service

Responsibilities

* Simulate clearing
* Generate pacs.002
* Publish settlement events

---

### Ledger Service

Responsibilities

* Update balances
* Record transactions
* Generate audit events

---

### AI Anomaly Detection

Responsibilities

* Calculate anomaly score
* Explain risk factors
* Raise suspicious transaction events

---

### Reporting Service

Responsibilities

Generate

* camt.052
* camt.053
* camt.054

---

### Resilience Monitor

Responsibilities

* Service health
* Incident recording
* Event replay
* Dependency monitoring

---

### Backoffice API

Responsibilities

* Search payments
* View incidents
* Audit history
* Dashboard endpoints

---

# 6. Runtime View

## Scenario 1

Successful Payment

```
Client

↓

POST /payments

↓

Payment Orchestrator

↓

pacs.008

↓

Kafka

↓

Settlement

↓

pacs.002

↓

Ledger Update

↓

AI Risk Score

↓

Audit Log

↓

Reporting
```

---

## Scenario 2

High-Risk Transaction

```
Payment

↓

AI Detection

↓

Risk Score > Threshold

↓

AnomalyDetected Event

↓

Incident Created

↓

Audit Trail
```

---

## Scenario 3

Settlement Failure

```
Settlement Timeout

↓

Retry

↓

Retry

↓

Retry

↓

Dead Letter Queue

↓

Incident

↓

Replay Endpoint
```

---

# 7. Deployment View

```
Docker

├── payment-orchestrator
├── settlement-service
├── ledger-service
├── reporting-service
├── anomaly-service
├── resilience-monitor
├── postgres
├── kafka
├── redis
├── prometheus
└── grafana
```

All services communicate through Kafka.

---

# 8. Cross-Cutting Concepts

## Security

* JWT Authentication
* OAuth2
* RBAC
* Correlation IDs

---

## Eventing

Kafka topics

* payment.initiated
* payment.validated
* settlement.completed
* settlement.failed
* anomaly.detected
* reporting.generated
* incident.created

---

## Audit

Every business event produces an immutable audit record.

---

## Observability

Metrics

* Payment latency
* Throughput
* Failed payments
* Retry count
* DLQ size
* Incident count

Logging

* Structured JSON
* Correlation IDs

Tracing

* OpenTelemetry

---

## AI Anomaly Detection

Current implementation

Explainable rule-based scoring.

Example signals

* Amount
* Velocity
* New beneficiary
* Time of day
* Country risk

Future enhancement

Replace scoring engine with ML inference service.

---

## DORA-Inspired Operational Resilience

Implemented concepts

* Dependency monitoring
* Health checks
* Incident management
* Immutable operational logs
* Retry policies
* DLQ
* Replay
* Service degradation
* Audit evidence

This showcase is inspired by DORA engineering principles and is not intended to represent regulatory compliance.

---

# 9. Architecture Decisions (ADRs)

Important architecture decisions are documented in dedicated ADR files.

## Completed ADRs

| # | Decision | File |
|---|----------|------|
| ADR-001 | Java 21 | [adr/ADR-001-java21.md](adr/ADR-001-java21.md) |
| ADR-002 | Event-Driven Architecture | [adr/ADR-002-event-driven-architecture.md](adr/ADR-002-event-driven-architecture.md) |
| ADR-003 | Kafka as Event Backbone | [adr/ADR-003-kafka-event-backbone.md](adr/ADR-003-kafka-event-backbone.md) |
| ADR-004 | PostgreSQL for Ledger | [adr/ADR-004-postgresql-ledger.md](adr/ADR-004-postgresql-ledger.md) |
| ADR-005 | Rule-Based AI Scoring | [adr/ADR-005-rule-based-ai-scoring.md](adr/ADR-005-rule-based-ai-scoring.md) |
| ADR-006 | Immutable Audit Trail | [adr/ADR-006-immutable-audit-trail.md](adr/ADR-006-immutable-audit-trail.md) |
| ADR-007 | Correlation ID Strategy | [adr/ADR-007-correlation-id-strategy.md](adr/ADR-007-correlation-id-strategy.md) |
| ADR-008 | Retry and Dead Letter Queue | [adr/ADR-008-retry-and-dead-letter-queue.md](adr/ADR-008-retry-and-dead-letter-queue.md) |
| ADR-009 | Docker-Based Local Deployment | [adr/ADR-009-docker-local-deployment.md](adr/ADR-009-docker-local-deployment.md) |

See [adr/README.md](adr/README.md) for ADR index and dependency graph.

---

# 10. Quality Requirements

| Quality Attribute | Implementation            |
| ----------------- | ------------------------- |
| Reliability       | Retry, DLQ                |
| Availability      | Health checks             |
| Security          | OAuth2, JWT               |
| Auditability      | Immutable events          |
| Maintainability   | Microservices             |
| Scalability       | Kafka                     |
| Extensibility     | Event-driven architecture |
| Observability     | Metrics, logs, tracing    |
| Recoverability    | Replay mechanism          |

---

# 11. Risks and Technical Debt

Current limitations

* Mock clearing
* Mock settlement
* Rule-based AI
* No real sanctions screening
* No PSD2 APIs
* Local deployment only

Future improvements

* ML model integration
* Fraud investigation workflow
* Multi-bank simulation
* Kubernetes deployment
* Open Banking APIs
* Chaos engineering
* mTLS between services
* Real-time dashboard

---

# 12. Glossary

| Term      | Description                        |
| --------- | ---------------------------------- |
| SCT       | SEPA Credit Transfer               |
| SCT Inst  | SEPA Instant Credit Transfer       |
| ISO 20022 | Financial messaging standard       |
| pacs.008  | Payment initiation                 |
| pacs.002  | Payment status                     |
| pacs.004  | Payment return                     |
| camt.052  | Intraday report                    |
| camt.053  | End-of-day statement               |
| camt.054  | Debit/Credit notification          |
| DLQ       | Dead Letter Queue                  |
| DORA      | Digital Operational Resilience Act |
