# ADR-006: Immutable Audit Trail

## Status
Accepted

## Context

Stockholm must demonstrate regulatory compliance and operational resilience patterns, particularly around auditability. Requirements:

- Complete history of all payment operations
- Immutable records (can't be changed retroactively)
- Correlated events showing causality
- Searchable audit evidence
- Non-repudiation (proof of who did what when)
- Compliance with European regulations (e.g., GDPR, PSD2, DORA)

Challenges:
- Audit data grows large over time
- Performance must not degrade
- Queries must be fast
- Historical analysis must be possible

## Decision

We have implemented an **immutable audit trail** using two complementary approaches:

### 1. Event Log (Kafka)
- Every business event published to Kafka is persisted
- Offset-based ordering guarantees causality
- Events can be replayed from any point
- Acts as the authoritative event record

### 2. Audit Table (PostgreSQL)
- Dedicated `audit_events` table
- Append-only (INSERT only, never UPDATE/DELETE)
- Records:
  - Event type
  - Entity ID (payment, incident, etc.)
  - Actor (user/service)
  - Timestamp
  - Correlation ID
  - Full event payload (JSON)
  - Signature/hash for integrity

### 3. Immutable Properties
- Timestamps set server-side (not client)
- Correlation IDs link related events
- Event hash enables tampering detection
- Database triggers prevent modification
- Read-only views for audit queries

## Consequences

### Positive
- **Regulatory compliance**: Meets audit trail requirements
- **Non-repudiation**: Proves who performed actions
- **Debugging**: Complete history for troubleshooting
- **Forensics**: Can reconstruct any point in time
- **Event replay**: Can re-process from any historical point
- **Causality**: Correlation IDs show event relationships
- **Integrity**: Hash verification detects tampering
- **Transparency**: Complete visibility into system operations

### Negative
- **Storage overhead**: Audit data grows unbounded
- **Query performance**: Large tables may slow queries
- **Privacy concerns**: Records sensitive payment details
- **Retention policy**: Must define data lifetime
- **Performance impact**: Writing audit records adds latency

### Trade-offs
- Chose compliance and auditability over minimal storage
- Accepted latency overhead for non-repudiation guarantees
- Retained all data (consider future GDPR right-to-be-forgotten)

## Alternatives Considered

### Minimal Audit (Just Errors)
- **Pros**: Less storage, simpler
- **Cons**: Insufficient for regulatory requirements

### Database Triggers Only
- **Pros**: Automatic tracking
- **Cons**: Database-specific, harder to integrate with events

### Separate Audit Service
- **Pros**: Decoupled from main operations
- **Cons**: Distributed coordination complexity

### Real-Time Archival
- **Pros**: Can delete from main DB
- **Cons**: Complex, operational overhead

## Decision Drivers

1. **Regulatory requirement**: Financial audits are mandatory
2. **DORA compliance**: Operational logs required
3. **Event sourcing**: Events are natural audit trail
4. **Debugging necessity**: Production issues require full context
5. **Non-repudiation**: Business users must trust system integrity

## Implementation Details

### Audit Event Structure
```
CREATE TABLE audit_events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100),
    entity_id VARCHAR(50),
    entity_type VARCHAR(50),
    actor VARCHAR(100),
    correlation_id UUID,
    timestamp TIMESTAMP,
    payload JSONB,
    event_hash VARCHAR(256),
    source_system VARCHAR(50),
    CONSTRAINT immutable_check CHECK (true)
);
```

### Correlation Strategy (ADR-007)
- All related events share same correlation ID
- Enables tracing complete payment lifecycle
- Links payment → settlement → ledger → reporting

### Retention Policy
- Development: No retention limit
- Production: Configurable (recommend 7 years per regulations)
- Archival: Old records moved to cold storage

## Related Decisions

- Depends on ADR-002 (Event-Driven Architecture) - Events form audit trail
- Depends on ADR-004 (PostgreSQL) - Storage layer
- Works with ADR-007 (Correlation ID Strategy) - Event linking
- Enables DORA operational resilience

