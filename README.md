# Stockholm

> Event-Driven SEPA Payment Orchestrator with AI-Assisted Transaction Anomaly Detection and DORA-Inspired Operational Resilience

---

## Overview

Stockholm is a software architecture showcase that demonstrates how a modern European payment platform can orchestrate SEPA Instant Credit Transfers using ISO 20022 while embracing event-driven architecture, operational resilience, and AI-assisted transaction monitoring.

The project focuses on architectural design rather than banking product implementation. All external financial networks are simulated using lightweight mock services, allowing the entire payment lifecycle to be demonstrated locally.

The showcase combines several important architecture topics frequently found in European banking systems:

- ISO 20022 payment processing
- Event-driven microservices
- Transaction orchestration
- AI-assisted anomaly detection
- Operational resilience inspired by DORA
- Immutable audit trails
- Observability and distributed tracing

---

# Objectives

The project demonstrates how to build a resilient payment platform that:

- processes SEPA Instant Credit Transfers
- generates ISO 20022 payment messages
- orchestrates asynchronous payment processing
- detects suspicious transaction patterns
- records immutable audit evidence
- survives infrastructure failures
- supports operational monitoring
- produces payment reporting

Rather than implementing every banking feature, Stockholm focuses on clean architecture and production-ready engineering practices.

---

# Architecture Principles

- Domain-driven design
- Event-driven architecture
- Loose coupling
- Asynchronous messaging
- Immutable events
- Idempotent processing
- Cloud-native architecture
- Observability by design
- Security by default
- Failure as a first-class citizen

---

# Features

## Payment Processing

- Payment initiation
- Payment validation
- Idempotency protection
- Payment orchestration
- Settlement simulation
- Payment return simulation
- Status tracking

---

## ISO 20022 Support

Generated example messages include:

- pacs.008
- pacs.002
- pacs.004
- camt.052
- camt.053
- camt.054

All messages are generated locally for demonstration purposes.

---

## Event-Driven Processing

Kafka is used as the event backbone.

Example events:

```
PaymentInitiated
PaymentValidated
SettlementCompleted
SettlementFailed
PaymentReturned
LedgerUpdated
AnomalyDetected
IncidentCreated
ReportGenerated
```

---

## AI-Assisted Transaction Monitoring

Stockholm includes a lightweight anomaly detection service.

Instead of using a trained machine learning model, the showcase implements an explainable risk scoring engine based on transaction characteristics.

Example signals include:

- unusually high amount
- high payment frequency
- new beneficiary
- repeated failed transactions
- unusual payment time
- high-risk destination

Example output:

```
Risk Score : 84

Reasons

✓ High transaction amount
✓ New beneficiary
✓ Rapid payment frequency
```

The architecture allows future replacement of the scoring engine with a machine learning model without changing upstream services.

---

# DORA-Inspired Operational Resilience

Stockholm demonstrates architectural patterns inspired by the Digital Operational Resilience Act (DORA).

Implemented concepts include:

## ICT Risk Management

- dependency inventory
- service health monitoring
- operational dashboards

## Incident Management

- incident creation
- incident timeline
- correlation IDs
- operational event log

## Operational Resilience

- retry policies
- dead-letter queues
- event replay
- circuit breaker
- graceful degradation

## Auditability

- immutable audit events
- complete payment timeline
- event history

## Third-Party Dependency Monitoring

Mock external providers expose health endpoints to simulate dependency failures.

---

# Technology Stack

| Area | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot |
| Build | Maven |
| Messaging | Kafka |
| Database | PostgreSQL |
| Cache | Redis |
| Security | Spring Security + JWT |
| IAM | Keycloak |
| Container | Docker |
| Optional | Kubernetes |
| Monitoring | Prometheus |
| Dashboard | Grafana |
| Tracing | OpenTelemetry |
| Logging | Loki / ELK |

---

# High-Level Architecture

```
                  Client
                     │
             REST API Gateway
                     │
          Payment Orchestrator
                     │
        ───────── Kafka ─────────
                     │
     ┌───────────────┼─────────────────┐
     │               │                 │
Settlement      Ledger Service   Reporting
 Service                           Service
     │
     │
Anomaly Detection
     │
     │
Resilience Monitor
     │
Backoffice API
```

---

# Payment Flow

```
Client

   │

POST /payments

   │

Generate pacs.008

   │

Publish PaymentInitiated

   │

Mock Clearing Service

   │

Generate pacs.002

   │

Update Ledger

   │

Calculate Risk Score

   │

Create Audit Events

   │

Generate camt Reports
```

---

# Repository Structure

```
stockholm/

├── docs/
│   ├── arc42.md
│   ├── adr/
│   ├── diagrams/
│   └── runtime/
│
├── services/
│   ├── payment-orchestrator/
│   ├── settlement-service/
│   ├── ledger-service/
│   ├── reporting-service/
│   ├── anomaly-detection-service/
│   ├── resilience-monitor/
│   └── backoffice-api/
│
├── samples/
│   ├── pacs008.xml
│   ├── pacs002.xml
│   ├── pacs004.xml
│   ├── camt052.xml
│   ├── camt053.xml
│   └── camt054.xml
│
├── docker-compose.yml
├── README.md
└── pom.xml
```

---

# Running Locally

Start infrastructure

```bash
docker compose up -d
```

Infrastructure includes:

- Kafka
- PostgreSQL
- Redis
- Keycloak
- Prometheus
- Grafana

Run the application

```bash
mvn spring-boot:run
```

or

```bash
docker compose up
```

---

# Quality Attributes

Stockholm is designed around the following quality attributes:

- Reliability
- Availability
- Auditability
- Scalability
- Observability
- Security
- Maintainability
- Extensibility
- Recoverability

---

# Resilience Patterns

- Idempotency Key
- Retry with Exponential Backoff
- Dead Letter Queue
- Event Replay
- Circuit Breaker
- Correlation ID
- Immutable Audit Trail
- Health Monitoring

---

# Security

- OAuth2 Authentication
- JWT Authorization
- RBAC
- API Protection
- Audit Logging
- Secure Service Communication

---

# Disclaimer

Stockholm is an educational software architecture showcase.

The project does not connect to real financial infrastructure, including:

- TIPS
- RT1
- TARGET Services
- SWIFT
- ECB infrastructure
- Commercial banks

All clearing, settlement, reporting, AI analysis, and operational events are simulated using local mock services.

---

# Documentation

Architecture documentation follows the arc42 template.

Included documentation:

- Architecture Overview
- Context Diagram
- Container Diagram
- Runtime Scenarios
- Deployment Diagram
- ADRs
- Sequence Diagrams
- Quality Scenarios

Documentation is located under:

```
docs/
```

---

# Author

**Slamet Widodo**

Software Architect specializing in:

- Banking Platforms
- Payment Systems
- ISO 8583
- ISO 20022
- Event-Driven Architecture
- Cloud-Native Systems
- Java & Spring Boot
- Financial System Integration

---

# License

MIT License