# ADR-007: Correlation ID Strategy for Distributed Tracing

## Status
Accepted

## Context

Stockholm operates as a microservices system with events flowing across multiple services:

- Payment Orchestrator initiates
- Settlement Service processes
- Ledger Service updates balances
- Reporting Service generates reports
- Anomaly Detection Service analyzes
- Resilience Monitor tracks

When issues occur, operators need to:
- Trace requests end-to-end
- Correlate logs across services
- Understand causality of events
- Debug failures quickly
- Reconstruct complete payment lifecycle

Without correlation, logs from different services are disconnected, making debugging nearly impossible at scale.

## Decision

We have implemented a **correlation ID strategy** using UUIDs propagated across all services.

### Correlation ID Flow

1. **Generation**: Payment Orchestrator generates UUID at request entry point
2. **Propagation**: Included in all events, logs, and calls
3. **Persistence**: Stored in audit trail, payment records, incidents
4. **Distributed tracing**: Passed to observability systems (OpenTelemetry)

### Propagation Paths

- **Events**: Every Kafka event carries correlation ID in headers
- **Logs**: MDC (Mapped Diagnostic Context) includes correlation ID
- **HTTP**: Custom header `X-Correlation-ID` in REST calls
- **Database**: Stored in audit_events, payment records
- **Traces**: OpenTelemetry context propagation

### Correlation ID Format

- Format: UUID v4 (36 characters)
- Example: `550e8400-e29b-41d4-a716-446655440000`
- Immutable once generated (never changed through lifecycle)

## Consequences

### Positive
- **End-to-end tracing**: Follow single payment through all services
- **Debugging efficiency**: Find all logs for a payment instantly
- **Root cause analysis**: Understand event chains
- **Operational visibility**: See payment status at each step
- **Performance profiling**: Identify slow paths
- **Audit compliance**: Links all audit events to originating request
- **Alert correlation**: Incidents tied to causative events
- **Log aggregation**: Tools like ELK, Loki can correlate logs

### Negative
- **Thread safety**: Must propagate through async contexts (mitigated by Spring support)
- **Storage overhead**: Small amount of extra data per record
- **MDC pollution**: If not managed carefully, can complicate logging
- **Backward compatibility**: Legacy integrations must be updated

### Trade-offs
- Chose operational visibility over minimal logging overhead
- Accepted propagation complexity for debugging benefits

## Alternatives Considered

### Transaction IDs (Smaller)
- **Pros**: Less storage
- **Cons**: UUID standard in industry, better for global uniqueness

### Timestamp-Based Correlation
- **Pros**: Simpler to generate
- **Cons**: Multiple simultaneous operations can't be distinguished

### Request Path Tracking
- **Pros**: Automatic via HTTP tracing
- **Cons**: Doesn't work for async/event-driven flows

### No Correlation
- **Pros**: Zero overhead
- **Cons**: Distributed debugging nearly impossible

## Decision Drivers

1. **Microservices complexity**: Multiple services inherently need correlation
2. **Event-driven architecture**: Async events need tracing
3. **Financial systems**: Audit trail requires linking events
4. **Operational necessity**: Must debug production issues
5. **Industry standard**: UUID correlation widely used in microservices

## Implementation Details

### Spring Boot Integration

```java
// Automatic via Spring Cloud Sleuth
@Component
public class CorrelationIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) {
        String correlationId = MDC.get("correlation_id")
            ?? UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        // ... continue chain
    }
}
```

### Kafka Event Headers
- Correlation ID in event headers
- Automatically extracted and set in MDC
- Available in consumer context

### Database Integration
- `correlation_id` column in audit_events
- Indexed for fast query by payment lifecycle
- Enables "show me everything for this payment" queries

### Logging Format
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "level": "INFO",
  "correlation_id": "550e8400-e29b-41d4-a716-446655440000",
  "service": "payment-orchestrator",
  "message": "Payment initiated",
  "payment_id": "PAY-12345"
}
```

### OpenTelemetry Integration
- Correlation ID becomes trace ID context
- Enables integration with distributed tracing systems
- Spans linked through same correlation ID
- Future Jaeger/Tempo support uses this

## Propagation Rules

1. If request has `X-Correlation-ID` header → reuse
2. If event has correlation ID → propagate
3. If neither → generate new UUID
4. Never modify correlation ID (always pass through unchanged)

## Related Decisions

- Depends on ADR-002 (Event-Driven Architecture) - Needed for event correlation
- Depends on ADR-003 (Kafka) - Correlation ID in event headers
- Depends on ADR-006 (Immutable Audit Trail) - Links audit events
- Enables observability across ADR-001-009

## Monitoring Use Cases

Examples of queries using correlation ID:

```sql
-- Show all audit events for a payment
SELECT * FROM audit_events
WHERE correlation_id = 'UUID'
ORDER BY timestamp;

-- Find payments with specific pattern
SELECT * FROM payments
WHERE correlation_id IN (
    SELECT DISTINCT correlation_id FROM anomaly_alerts
    WHERE risk_score > 80
);

-- Timeline of incidents
SELECT * FROM incidents
WHERE correlation_id = 'UUID'
ORDER BY created_at;
```

