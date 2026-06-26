# ADR-003: Kafka as Event Backbone

## Status
Accepted

## Context

Stockholm uses event-driven architecture (ADR-002) requiring a reliable, scalable message broker that:

- Supports pub-sub patterns with multiple consumers
- Provides message ordering and durability
- Enables event replay for resilience
- Scales horizontally across services
- Integrates with Spring Boot ecosystem
- Supports local development with Docker

Multiple events flow through the system simultaneously:
- Payment events from orchestrator
- Settlement events from clearing service
- Ledger updates
- Anomaly detection alerts
- Reporting generation

## Decision

We have chosen **Apache Kafka** as the central event backbone.

Kafka topics established:
- `payment.initiated` - New payment requests
- `payment.validated` - Validated payments
- `settlement.completed` - Successful settlements
- `settlement.failed` - Failed settlements
- `ledger.updated` - Balance updates
- `anomaly.detected` - Suspicious transactions
- `reporting.generated` - Report completion
- `incident.created` - Operational incidents

Each service subscribes to relevant topics and produces events to others.

## Consequences

### Positive
- **Pub-Sub capabilities**: Multiple consumers per topic, independent processing
- **Event replay**: Offset tracking enables reprocessing from specific points
- **Durability**: Events persisted locally, enabling recovery
- **Ordering guarantees**: Per-partition ordering maintains causality
- **Scalability**: Horizontal scaling through partitioning
- **Operational maturity**: Battle-tested in production systems
- **Spring Integration**: Spring Kafka provides seamless integration
- **Local development**: Docker-compose enables easy local setup
- **Dead Letter Topics**: Built-in support for failure handling

### Negative
- **Operational complexity**: Requires cluster management and monitoring
- **Storage overhead**: Events stored durably consume disk space
- **Learning curve**: Kafka concepts (partitions, offsets, rebalancing) need understanding
- **Latency**: Not optimal for ultra-low-latency scenarios (though acceptable for payments)

### Trade-offs
- Chose reliability and replay capability over simpler message brokers
- Accepted storage overhead for durability guarantees

## Alternatives Considered

### RabbitMQ
- **Pros**: Lighter weight, easier operations
- **Cons**: Limited replay capabilities, less suitable for event sourcing patterns

### AWS SNS/SQS
- **Pros**: Managed service, no operational overhead
- **Cons**: Cloud-vendor lock-in, incompatible with local development goal

### Redis Streams
- **Pros**: Lightweight, fast
- **Cons**: Less durable, smaller community for financial systems

### Apache Pulsar
- **Pros**: Multi-tenancy, better isolation
- **Cons**: More complex, heavier resource requirements

## Decision Drivers

1. **Replay requirements**: DORA resilience patterns need event replay
2. **Auditability**: Financial systems require durable event trails
3. **Scale**: Partition-based scaling aligns with future growth
4. **Industry standard**: Kafka widely adopted in financial services
5. **Local development**: Docker support critical for showcase goals

## Implementation Details

- Kafka runs in Docker for local development
- Partition strategy: One partition per logical domain initially, can scale horizontally
- Retention policy: Events retained for 7 days in development (configurable)
- Replication factor: Configured for reliability
- Consumer groups: Each service maintains independent consumer group

## Related Decisions

- Depends on ADR-002 (Event-Driven Architecture)
- Enables ADR-008 (Retry and Dead Letter Queue)
- Enables ADR-007 (Correlation ID Strategy)

