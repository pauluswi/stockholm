# Architecture Decision Records (ADRs)

This directory contains Architecture Decision Records (ADRs) for the Stockholm project. Each ADR documents an important architectural decision, including the context, decision, consequences, and alternatives considered.

## ADR Index

| # | Title | Status | Category |
|---|-------|--------|----------|
| [ADR-001](./ADR-001-java21.md) | Java 21 as Primary Language | Accepted | Technology |
| [ADR-002](./ADR-002-event-driven-architecture.md) | Event-Driven Architecture | Accepted | Architecture |
| [ADR-003](./ADR-003-kafka-event-backbone.md) | Kafka as Event Backbone | Accepted | Infrastructure |
| [ADR-004](./ADR-004-postgresql-ledger.md) | PostgreSQL for Ledger and State | Accepted | Infrastructure |
| [ADR-005](./ADR-005-rule-based-ai-scoring.md) | Rule-Based AI Scoring for Anomaly Detection | Accepted | AI/ML |
| [ADR-006](./ADR-006-immutable-audit-trail.md) | Immutable Audit Trail | Accepted | Resilience |
| [ADR-007](./ADR-007-correlation-id-strategy.md) | Correlation ID Strategy for Distributed Tracing | Accepted | Observability |
| [ADR-008](./ADR-008-retry-and-dead-letter-queue.md) | Retry and Dead Letter Queue Strategy | Accepted | Resilience |
| [ADR-009](./ADR-009-docker-local-deployment.md) | Docker-Based Local Deployment | Accepted | Deployment |

## How to Use ADRs

- **New Decision?** Create a new ADR following the template
- **Understanding**: Each ADR section explains the reasoning
- **Context**: Dependencies between ADRs are documented
- **Reference**: Arc42 section 9 references these ADRs

## ADR Dependencies

```
ADR-001 (Java)
    ↓
ADR-002 (Event-Driven)
    ├→ ADR-003 (Kafka)
    ├→ ADR-006 (Audit Trail)
    └→ ADR-007 (Correlation IDs)

ADR-004 (PostgreSQL)
    ↓
ADR-006 (Audit Trail)

ADR-003 (Kafka) + ADR-008 (Retry/DLQ)
    ↓
ADR-007 (Correlation IDs)

ADR-005 (AI Scoring)
    ↓
ADR-002 (Event-Driven)

ADR-009 (Docker)
    ↓
All others (enables deployment)
```

## Status Legend

- **Accepted**: Decision made and implemented
- **Proposed**: Under review
- **Deprecated**: No longer used
- **Superseded**: Replaced by another ADR

## References

- [arc42 Section 9: Architecture Decisions](../arc42.md#9-architecture-decisions-adrs)
- [README.md](../../README.md)
- Design documentation in `../diagrams/`

---

**Last Updated**: June 26, 2026
**Author**: Slamet Widodo

