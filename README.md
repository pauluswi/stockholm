# Stockholm

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?logo=springboot&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Kafka-Event%20Driven-231F20?logo=apachekafka&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

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

Sample XML templates are available in [`samples/`](samples/):

- [`pacs008.xml`](samples/pacs008.xml)
- [`pacs002.xml`](samples/pacs002.xml)
- [`pacs004.xml`](samples/pacs004.xml)
- [`camt052.xml`](samples/camt052.xml)
- [`camt053.xml`](samples/camt053.xml)
- [`camt054.xml`](samples/camt054.xml)

Sample message snippets:

```xml
<!-- pacs.008: Customer Credit Transfer -->
<Document>
  <CdtTrfTxInf>
    <PmtId><EndToEndId>E2E-2026-0001</EndToEndId></PmtId>
    <Amt><InstdAmt Ccy="EUR">5000.00</InstdAmt></Amt>
  </CdtTrfTxInf>
</Document>

<!-- pacs.002: Payment Status Report -->
<Document>
  <TxInfAndSts>
    <OrgnlEndToEndId>E2E-2026-0001</OrgnlEndToEndId>
    <TxSts>ACCP</TxSts>
  </TxInfAndSts>
</Document>

<!-- pacs.004: Payment Return -->
<Document>
  <RtrTxInf>
    <OrgnlEndToEndId>E2E-2026-0001</OrgnlEndToEndId>
    <RtrRsnInf><Rsn><Cd>AC04</Cd></Rsn></RtrRsnInf>
  </RtrTxInf>
</Document>

<!-- camt.052: Intraday Report -->
<Document>
  <Rpt>
    <Acct><Id><IBAN>DE02120300000000202051</IBAN></Id></Acct>
  </Rpt>
</Document>

<!-- camt.053: End-of-Day Statement -->
<Document>
  <Stmt>
    <Bal><Amt Ccy="EUR">12500.00</Amt></Bal>
  </Stmt>
</Document>

<!-- camt.054: Debit/Credit Notification -->
<Document>
  <Ntfctn>
    <Ntry><Amt Ccy="EUR">5000.00</Amt></Ntry>
  </Ntfctn>
</Document>
```

All messages are generated locally for demonstration purposes.

---

## Event-Driven Processing

Kafka is used as the event backbone.

The core happy-path event chain is:

```text
payment.initiated → settlement.completed → ledger.updated → reporting-service
```

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

### Example Kafka Message

`payment.initiated` topic payload:

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440001",
  "eventType": "PaymentInitiatedEvent",
  "correlationId": "corr-12345-abcde",
  "timestamp": "2026-07-04T10:30:00Z",
  "paymentId": "PAY-2026-0001",
  "orderer": "ACME Corp",
  "beneficiary": "TechCorp GmbH",
  "amount": 5000.00,
  "currency": "EUR"
}
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

Stockholm demonstrates architectural patterns inspired by the Digital Operational Resilience Act (DORA).

- dependency inventory
- service health monitoring
- operational dashboards

The event chain for the core payment flow is:

```text
Payment Orchestrator → payment.initiated → Settlement Service
Settlement Service → settlement.completed → Ledger Service
Ledger Service → ledger.updated → Reporting Service
```

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
      ├──────────────► Settlement Service ───────────► Ledger Service ───────────► Reporting Service
      │
      ├──────────────► Anomaly Detection Service
      │
      ├──────────────► Resilience Monitor
      │
      └──────────────► Backoffice API (read model queries)
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

Publish LedgerUpdated

   │

Generate reporting outputs

   │

Calculate Risk Score

   │

Create Audit Events
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

- Zookeeper
- Kafka
- PostgreSQL
- Redis
- Anomaly Detection Service (8087)
- Reporting Service (8086)
- Resilience Monitor (8088)
- Backoffice API (8089)

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

Documentation is located under: [docs/architecture](docs/architecture)

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