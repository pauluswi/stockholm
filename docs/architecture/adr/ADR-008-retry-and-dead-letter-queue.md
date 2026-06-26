# ADR-008: Retry and Dead Letter Queue Strategy

## Status
Accepted

## Context

Stockholm operates as a distributed event-driven system where failures are inevitable:

- Services crash or restart
- Network timeouts occur
- External dependencies fail
- Databases become temporarily unavailable
- Messages can't be processed immediately

Example failure scenarios:
- Settlement service temporarily unavailable
- Ledger database connection lost
- Anomaly detection service overloaded
- Message processing logic has bugs

Without a resilience strategy, failed payments would be lost or require manual intervention. We need:

- Automatic retry on transient failures
- Detection of permanent failures
- Manual review capability for failed transactions
- Recovery mechanism to replay failed work
- Operational visibility into failures

## Decision

We have implemented a **retry and dead letter queue strategy**:

### Retry Mechanism

**Exponential Backoff Retry**

1. **First attempt**: Immediate processing
2. **First failure**: Retry after 1 second
3. **Second failure**: Retry after 2 seconds (exponential)
4. **Third failure**: Retry after 4 seconds
5. **Fourth failure**: Retry after 8 seconds
6. **Fifth failure**: Retry after 16 seconds
7. **Final failure**: Send to Dead Letter Queue

### Dead Letter Queue

- Separate Kafka topic: `{topic-name}.dlq`
- Collects all permanently failed messages
- Includes:
  - Original message
  - Error details
  - Retry count
  - Last attempt timestamp
  - Stack trace

### Resilience Patterns

1. **Idempotency**: All operations safe to retry
2. **Circuit breaker**: Detect cascading failures
3. **Fallback**: Gracefully degrade on dependency failure
4. **Health check**: Verify service availability before retrying

## Consequences

### Positive
- **Resilience**: Transient failures don't lose transactions
- **Autonomy**: Services recover without manual intervention
- **Visibility**: Failed messages queued for review
- **Recovery**: DLQ messages can be replayed
- **Adaptation**: System automatically adapts to temporary outages
- **Operational**: Backoff prevents overwhelming struggling services
- **Compliance**: Failed transactions tracked and audited
- **Safety**: Exponential backoff prevents thundering herd

### Negative
- **Latency**: Retries add delay to failed transactions
- **Complexity**: Idempotency requirements throughout system
- **Storage**: DLQ can grow large if recovery not automated
- **Cascading failures**: If root cause not fixed, DLQ fills up
- **Observability required**: Operators must monitor DLQ size

### Trade-offs
- Chose automatic recovery over immediate failure notification
- Accepted retry latency for resilience
- Requires careful idempotency design

## Alternatives Considered

### No Retry (Fail Fast)
- **Pros**: Simple, immediate feedback
- **Cons**: Loses payments on transient failures

### Infinite Retry
- **Pros**: Eventually succeeds
- **Cons**: Can create backlog, never detects permanent issues

### Linear Backoff
- **Pros**: Simpler to understand
- **Cons**: Can still overwhelm struggling services

### Circuit Breaker Only
- **Pros**: Prevents cascading failures
- **Cons**: Doesn't handle transient timeouts

## Decision Drivers

1. **Payment criticality**: Can't lose payments on transient failures
2. **Distributed systems**: Temporary failures are normal
3. **DORA resilience**: Must demonstrate failure recovery
4. **Operational visibility**: Operators need view of failures
5. **Financial accuracy**: Every payment must be accounted for

## Configuration Details

### Retry Policy

```
Max retries: 5
Backoff multiplier: 2.0
Initial delay: 1 second
Max delay: 30 seconds
Jitter: ±10% (prevent thundering herd)
```

### DLQ Topics

```
payment.initiated → payment.initiated.dlq
settlement.completed → settlement.completed.dlq
ledger.updated → ledger.updated.dlq
anomaly.detected → anomaly.detected.dlq
```

### Failure Scenarios and Handling

| Scenario | Detection | Retry | Outcome |
|----------|-----------|-------|---------|
| Network timeout | IOException | Yes (auto) | Success after recovery |
| Database unavailable | Connection refused | Yes (auto) | Success after restart |
| Logic bug | Exception thrown | Limited | DLQ (requires code fix) |
| Invalid message | Parsing error | No | DLQ (manual review) |
| Dependency down | Circuit open | No | DLQ (wait for recovery) |

## Implementation Pattern

### Consumer Configuration

```java
@KafkaListener(topics = "payment.initiated",
              groupId = "settlement-service")
public void handlePaymentInitiated(PaymentInitiated event) {
    try {
        settlementService.process(event);
    } catch (TemporaryException e) {
        // Kafka framework retries automatically
        throw e;
    } catch (PermanentException e) {
        // Send to DLQ
        dlqPublisher.send(event, e);
    }
}
```

### Idempotency Implementation

All message handlers must be idempotent:

```java
public void processPayment(PaymentInitiated event) {
    // Check if already processed (idempotency key)
    if (ledger.paymentExists(event.getPaymentId())) {
        return; // Already processed, safe to skip
    }

    // Process payment
    ledger.recordPayment(event);
}
```

### DLQ Monitoring

Backoffice API exposes DLQ metrics:
- Messages in DLQ by topic
- Age of oldest message
- Trend over time
- Alert if growing too fast

### Recovery Process

1. **Investigate**: Determine root cause
2. **Fix**: Deploy code or operational fix
3. **Query**: Find messages in DLQ matching cause
4. **Replay**: Move messages back to original topic
5. **Monitor**: Verify successful processing

## Operational Procedures

### DLQ Growing (Incident)

1. Alert triggered: DLQ size > threshold
2. Investigate: Check error logs, service health
3. Remediate: Fix root cause
4. Replay: Move messages from DLQ back to main topic
5. Verify: Confirm messages processed successfully

### Manual Replay

```
POST /backoffice/api/replay
{
  "topic": "settlement.completed",
  "dlq": true,
  "count": 100,
  "filter": { "timestamp": ">2024-01-01" }
}
```

## Related Decisions

- Depends on ADR-002 (Event-Driven Architecture) - Retry pattern within events
- Depends on ADR-003 (Kafka) - DLQ topics and reprocessing
- Works with ADR-006 (Immutable Audit Trail) - All retries audited
- Works with ADR-007 (Correlation ID Strategy) - Retries tracked via correlation ID
- Enables DORA resilience requirements

