# ADR-002: Event-Driven Architecture

## Status
Accepted

## Context

Stockholm must demonstrate how modern payment platforms orchestrate complex, asynchronous processes across multiple services. Payment orchestration involves:

- Multiple independent operations (settlement, reporting, monitoring)
- Uncertain timing and ordering
- Distributed state management
- Need for auditability and traceability
- Potential for service failures and recovery

Traditional request-response architectures create tight coupling and complex failure scenarios. An event-driven model provides loose coupling and better resilience.

## Decision

We have chosen an **event-driven architecture** as the primary communication pattern.

All significant business operations produce immutable events that trigger downstream processing:
- PaymentInitiated
- PaymentValidated
- SettlementCompleted
- SettlementFailed
- AnomalyDetected
- LedgerUpdated
- IncidentCreated
- ReportGenerated

Services communicate via published events rather than direct calls, enabling asynchronous, loosely-coupled workflows.

## Consequences

### Positive
- **Loose coupling**: Services depend on events, not on each other
- **Scalability**: Services process events at their own pace
- **Auditability**: Every business action produces immutable evidence
- **Resilience**: Failed processing can be replayed
- **Extensibility**: New services can subscribe to existing events
- **Observability**: Complete event trail enables tracing
- **Async processing**: Better resource utilization

### Negative
- **Eventual consistency**: Services become eventually consistent, not immediately consistent
- **Debugging complexity**: Distributed workflows harder to trace
- **Latency**: Multiple hops may increase end-to-end latency
- **Operational overhead**: Requires robust event infrastructure

### Trade-offs
- Prioritized resilience and scalability over immediate consistency
- Accepted debugging complexity for operational benefits

## Alternatives Considered

### Synchronous Request-Response
- **Pros**: Simpler to understand, immediate feedback
- **Cons**: Tight coupling, cascading failures, harder to scale

### Hybrid Synchronous + Asynchronous
- **Pros**: Best of both worlds
- **Cons**: Increased architectural complexity, harder to reason about

### Message Queue (Point-to-Point)
- **Pros**: Simpler than pub-sub
- **Cons**: Less flexible for multi-consumer scenarios

## Decision Drivers

1. **Payment workflows are inherently asynchronous** - Settlement takes time, risk analysis is separate
2. **DORA resilience requires event replay** - Event backbone enables recovery
3. **Auditability demands immutable trails** - Events provide natural audit log
4. **Multiple independent services** - Event-driven enables clean separation

## Relationship to Other Decisions

- Depends on ADR-003 (Kafka)
- Enables ADR-006 (Immutable Audit Trail)
- Enables ADR-007 (Correlation ID Strategy)
- Enabled by ADR-008 (Retry and DLQ)

