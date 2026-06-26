# Stockholm Documentation Index

Complete documentation for the Stockholm Event-Driven SEPA Payment Orchestrator platform.

---

## 📚 Documentation Structure

```
stockholm/
├── README.md                          ← Project overview
└── docs/
    └── architecture/
        ├── arc42.md                   ← Full architecture documentation
        ├── adr/                       ← Architecture Decision Records
        │   ├── README.md              ← ADR index
        │   ├── ADR-001-java21.md
        │   ├── ADR-002-event-driven-architecture.md
        │   ├── ADR-003-kafka-event-backbone.md
        │   ├── ADR-004-postgresql-ledger.md
        │   ├── ADR-005-rule-based-ai-scoring.md
        │   ├── ADR-006-immutable-audit-trail.md
        │   ├── ADR-007-correlation-id-strategy.md
        │   ├── ADR-008-retry-and-dead-letter-queue.md
        │   └── ADR-009-docker-local-deployment.md
        └── diagrams/                  ← Visual architecture diagrams
            ├── README.md              ← Diagrams index
            ├── 01-context-diagram.md
            ├── 02-container-diagram.md
            ├── 03-sequence-diagrams.md
            └── 04-deployment-diagram.md
```

---

## 🎯 Quick Start by Role

### For Project Managers / Business Stakeholders

1. **Start here**: [README.md](README.md)
   - Understand what Stockholm does
   - See key features and capabilities
   - Learn technology stack

2. **Then read**: [Context Diagram](docs/architecture/diagrams/01-context-diagram.md)
   - See external systems integration
   - Understand user interactions
   - Data flows overview

### For Software Architects

1. **Start here**: [arc42.md](docs/architecture/arc42.md) (sections 1-4)
   - Goals, constraints, strategy
   - Quality attributes

2. **Architecture decisions**: [ADR Index](docs/architecture/adr/README.md)
   - Why each key decision was made
   - Alternatives considered
   - Consequences documented

3. **Visual overview**: [Container Diagram](docs/architecture/diagrams/02-container-diagram.md)
   - All microservices and infrastructure
   - Communication patterns
   - Technology choices per component

### For Software Engineers

1. **System overview**: [Container Diagram](docs/architecture/diagrams/02-container-diagram.md)
   - Service responsibilities
   - Technologies used
   - Data stores and messaging

2. **How it works**: [Sequence Diagrams](docs/architecture/diagrams/03-sequence-diagrams.md)
   - Payment flow examples
   - Anomaly detection flow
   - Failure and recovery scenarios

3. **Development setup**: [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md) - Local Development section
   - Docker Compose setup
   - Service startup
   - Connection details

4. **Specific decisions**: Individual [ADRs](docs/architecture/adr/README.md)
   - Why specific technologies chosen
   - Implementation patterns
   - Trade-offs documented

### For DevOps / Infrastructure Engineers

1. **Deployment options**: [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md)
   - Local development (Docker Compose)
   - Production (Docker containers)
   - Kubernetes (HA setup)
   - Multi-region DR configuration

2. **Docker setup**: [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md) - Local Development section
   - docker-compose.yml structure
   - Service configurations
   - Health checks and dependencies

3. **Production deployment**: [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md) - Production section
   - Horizontal scaling
   - High availability
   - Disaster recovery

4. **Infrastructure decisions**: [ADR-009](docs/architecture/adr/ADR-009-docker-local-deployment.md)
   - Why Docker chosen
   - Kubernetes migration path
   - CI/CD integration

---

## 📖 Complete Documentation Map

### Level 1: Overview & Context

| Document | Purpose | Audience |
|----------|---------|----------|
| [README.md](README.md) | Project overview, features, technology stack | Everyone |
| [Context Diagram](docs/architecture/diagrams/01-context-diagram.md) | System boundaries, external systems, user roles | Architects, PMs |

### Level 2: Architecture Structure

| Document | Purpose | Audience |
|----------|---------|----------|
| [arc42.md](docs/architecture/arc42.md) - Sections 1-4 | Goals, constraints, strategy, principles | Architects, Tech Leads |
| [Container Diagram](docs/architecture/diagrams/02-container-diagram.md) | Microservices, databases, infrastructure | All engineers |
| [arc42.md](docs/architecture/arc42.md) - Section 5 | Building blocks and components | Architects, Engineers |

### Level 3: Decisions & Rationale

| Document | Purpose | Audience |
|----------|---------|----------|
| [ADR Index](docs/architecture/adr/README.md) | All decisions at a glance | Decision makers |
| [Individual ADRs](docs/architecture/adr/) (9 total) | Context, decision, consequences, alternatives | Architects, Engineers |
| [arc42.md](docs/architecture/arc42.md) - Section 9 | Links to all ADRs | Reference |

### Level 4: Runtime Behavior

| Document | Purpose | Audience |
|----------|---------|----------|
| [Sequence Diagrams](docs/architecture/diagrams/03-sequence-diagrams.md) | Payment flows, anomaly detection, failure recovery | All engineers |
| [arc42.md](docs/architecture/arc42.md) - Section 6 | Runtime scenarios | Architects, Engineers |

### Level 5: Deployment & Operations

| Document | Purpose | Audience |
|----------|---------|----------|
| [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md) | Local, production, Kubernetes, HA/DR | DevOps, Architects |
| [arc42.md](docs/architecture/arc42.md) - Section 7 | Deployment view | DevOps, Operations |
| [ADR-009](docs/architecture/adr/ADR-009-docker-local-deployment.md) | Docker & deployment decisions | DevOps, Architects |

### Level 6: Cross-Cutting Concerns

| Document | Purpose | Audience |
|----------|---------|----------|
| [arc42.md](docs/architecture/arc42.md) - Section 8 | Security, observability, audit, AI, resilience | All |
| [ADR-006](docs/architecture/adr/ADR-006-immutable-audit-trail.md) | Audit trail strategy | Architects, Compliance |
| [ADR-007](docs/architecture/adr/ADR-007-correlation-id-strategy.md) | Distributed tracing strategy | DevOps, Architects |
| [ADR-008](docs/architecture/adr/ADR-008-retry-and-dead-letter-queue.md) | Resilience patterns | All engineers |

---

## 🔗 Document Cross-References

### Understanding Payment Processing

1. Start: [README.md](README.md) - Payment Processing section
2. Context: [Context Diagram](docs/architecture/diagrams/01-context-diagram.md) - Data Flows section
3. Components: [Container Diagram](docs/architecture/diagrams/02-container-diagram.md) - Component Responsibilities
4. Flow: [Sequence Diagrams](docs/architecture/diagrams/03-sequence-diagrams.md) - Scenario 1
5. Details: [arc42.md](docs/architecture/arc42.md) - Section 6: Runtime View

### Understanding Anomaly Detection

1. Overview: [README.md](README.md) - AI-Assisted Transaction Monitoring
2. Architecture: [Container Diagram](docs/architecture/diagrams/02-container-diagram.md) - Anomaly Detection component
3. Flow: [Sequence Diagrams](docs/architecture/diagrams/03-sequence-diagrams.md) - Scenario 2: High-Risk Transaction
4. Decision: [ADR-005](docs/architecture/adr/ADR-005-rule-based-ai-scoring.md) - Rule-Based AI Scoring
5. Details: [arc42.md](docs/architecture/arc42.md) - Section 8: AI Anomaly Detection

### Understanding Resilience

1. Overview: [README.md](README.md) - DORA-Inspired Operational Resilience
2. Architecture: [Container Diagram](docs/architecture/diagrams/02-container-diagram.md) - Resilience Monitor component
3. Failures: [Sequence Diagrams](docs/architecture/diagrams/03-sequence-diagrams.md) - Scenario 3: Settlement Failure
4. Decisions: [ADR-008](docs/architecture/adr/ADR-008-retry-and-dead-letter-queue.md) - Retry & DLQ Strategy
5. Monitoring: [ADR-007](docs/architecture/adr/ADR-007-correlation-id-strategy.md) - Correlation ID for tracing
6. Audit: [ADR-006](docs/architecture/adr/ADR-006-immutable-audit-trail.md) - Immutable audit trail

### Understanding Deployment

1. Options: [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md)
2. Local: [README.md](README.md) - Running Locally section
3. Decision: [ADR-009](docs/architecture/adr/ADR-009-docker-local-deployment.md) - Docker-Based Deployment
4. Full details: [arc42.md](docs/architecture/arc42.md) - Section 7: Deployment View

---

## 📋 Documentation Statistics

### Completed

| Category | Count | Status |
|----------|-------|--------|
| Arc42 Sections | 12/12 | ✅ Complete |
| ADRs | 9/9 | ✅ Complete |
| Diagrams | 4/4 | ✅ Complete |
| Total Pages | 21 | ✅ Complete |

### Not Yet Documented

These are mentioned in arc42 as future documentation:
- Runtime scenarios (detailed procedures) - mentioned in arc42 Section 3
- ADR examples in different domains
- Detailed component-level design
- Source code level documentation (JavaDoc)
- API reference documentation

---

## 🎓 Learning Paths

### Path 1: Quick Overview (15 minutes)
1. [README.md](README.md) - Features & Overview
2. [Context Diagram](docs/architecture/diagrams/01-context-diagram.md) - System boundaries

### Path 2: Architecture Understanding (1 hour)
1. [README.md](README.md) - Full read
2. [arc42.md](docs/architecture/arc42.md) - Sections 1-4
3. [Container Diagram](docs/architecture/diagrams/02-container-diagram.md) - Full
4. [ADR Index](docs/architecture/adr/README.md) - Scan all

### Path 3: Complete Deep Dive (3 hours)
1. [README.md](README.md)
2. [arc42.md](docs/architecture/arc42.md) - All sections
3. [Context Diagram](docs/architecture/diagrams/01-context-diagram.md)
4. [Container Diagram](docs/architecture/diagrams/02-container-diagram.md)
5. [Sequence Diagrams](docs/architecture/diagrams/03-sequence-diagrams.md)
6. [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md)
7. Each [ADR](docs/architecture/adr/) - Read deeply

### Path 4: Developer Setup (30 minutes)
1. [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md) - Local Development
2. [README.md](README.md) - Running Locally section
3. [Container Diagram](docs/architecture/diagrams/02-container-diagram.md) - Technology stack

### Path 5: DevOps/Infrastructure (1 hour)
1. [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md) - All sections
2. [ADR-009](docs/architecture/adr/ADR-009-docker-local-deployment.md)
3. [arc42.md](docs/architecture/arc42.md) - Section 7

---

## 📝 Documentation Standards

### For Consistency

All documentation follows:
- **Arc42 template** for overall architecture
- **ADR format** for decisions (status, context, decision, consequences, alternatives)
- **C4 Model** for diagrams (context, container, component, code)
- **Mermaid syntax** for all visual diagrams (version-controllable)
- **Markdown format** for all text (GitHub-native rendering)

### For Maintenance

- Keep diagrams and text synchronized
- Update diagrams when architecture changes
- Document decisions as they're made (ADRs)
- Link related documentation
- Review changes in pull requests
- Keep dates updated in headers

---

## 🔄 Related Resources

- **Source Code**: [src/main/java/](../src/main/java/)
- **Tests**: [src/test/java/](../src/test/java/)
- **Configuration**: [docker-compose.yml](../docker-compose.yml)
- **Build**: [pom.xml](../pom.xml)

---

## ❓ Documentation Questions

### Where do I find...?

- **"How do I run this locally?"** → [README.md](README.md) Running Locally + [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md)
- **"Why did they choose X technology?"** → [ADR-001 through ADR-009](docs/architecture/adr/)
- **"What happens when a payment is processed?"** → [Sequence Diagrams](docs/architecture/diagrams/03-sequence-diagrams.md)
- **"How does anomaly detection work?"** → [ADR-005](docs/architecture/adr/ADR-005-rule-based-ai-scoring.md) + [Sequence Diagram Scenario 2](docs/architecture/diagrams/03-sequence-diagrams.md)
- **"What if a service fails?"** → [ADR-008](docs/architecture/adr/ADR-008-retry-and-dead-letter-queue.md) + [Sequence Diagram Scenario 3](docs/architecture/diagrams/03-sequence-diagrams.md)
- **"How is this deployed to Kubernetes?"** → [Deployment Diagram](docs/architecture/diagrams/04-deployment-diagram.md) Kubernetes section
- **"How do we maintain audit trails?"** → [ADR-006](docs/architecture/adr/ADR-006-immutable-audit-trail.md)
- **"How do we trace requests across services?"** → [ADR-007](docs/architecture/adr/ADR-007-correlation-id-strategy.md)

---

## 📅 Documentation Timeline

| Date | Milestone |
|------|-----------|
| June 26, 2026 | ✅ README.md written |
| June 26, 2026 | ✅ arc42.md completed (12 sections) |
| June 26, 2026 | ✅ All 9 ADRs created |
| June 26, 2026 | ✅ 4 architectural diagrams created |
| June 26, 2026 | ✅ Documentation index created |
| TBD | ⏳ Implementation begins |
| TBD | ⏳ API documentation |
| TBD | ⏳ Operational runbooks |

---

**Last Updated**: June 26, 2026
**Status**: Architecture documentation complete
**Next Phase**: Implementation

---

## 🤝 Contributing to Documentation

When making architectural changes:

1. **New decision?** → Create new ADR
2. **Changing design?** → Update affected diagrams
3. **New technology?** → Update arc42 section 2 (constraints)
4. **New feature?** → Add to sequence diagrams
5. **Always**: Commit with clear messages referencing ADRs

See individual document headers for update procedures.

