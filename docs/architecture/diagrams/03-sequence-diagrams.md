# Sequence Diagrams

## Scenario 1: Successful Payment Flow

Shows the complete lifecycle of a successful payment from initiation through reporting.

```mermaid
sequenceDiagram
    participant Client
    participant Orchestrator as Payment<br/>Orchestrator
    participant Kafka
    participant Settlement as Settlement<br/>Service
    participant Ledger as Ledger<br/>Service
    participant Anomaly as Anomaly<br/>Detection
    participant Reporting as Reporting<br/>Service
    participant Database as PostgreSQL

    Client->>Orchestrator: POST /payments<br/>{orderer, beneficiary, amount}

    activate Orchestrator
    Orchestrator->>Database: INSERT payment<br/>(status=INITIATED)
    Orchestrator->>Orchestrator: Generate UUID<br/>correlationId
    Orchestrator->>Orchestrator: Generate pacs.008
    Orchestrator->>Kafka: PublishEvent<br/>PaymentInitiated
    deactivate Orchestrator

    Orchestrator-->>Client: 201 Created<br/>{paymentId, status, correlationId}

    Note over Kafka: Event flows to subscribers

    activate Settlement
    Kafka->>Settlement: consume PaymentInitiated
    Settlement->>Settlement: Simulate clearing
    Settlement->>Settlement: Generate pacs.002
    Settlement->>Database: INSERT settlement_record
    Settlement->>Kafka: PublishEvent<br/>SettlementCompleted
    deactivate Settlement

    activate Ledger
    Kafka->>Ledger: consume SettlementCompleted
    Ledger->>Database: UPDATE account_balance
    Ledger->>Database: INSERT ledger_entry
    Ledger->>Kafka: PublishEvent<br/>LedgerUpdated
    deactivate Ledger

    activate Anomaly
    Kafka->>Anomaly: consume PaymentInitiated
    Anomaly->>Database: SELECT transaction_history
    Anomaly->>Anomaly: Calculate risk score<br/>Score: 32/100
    Anomaly->>Anomaly: Assess risk factors
    Anomaly->>Kafka: PublishEvent<br/>AnomalyScored<br/>(low risk)
    deactivate Anomaly

    activate Reporting
    Kafka->>Reporting: consume LedgerUpdated
    Reporting->>Database: SELECT all_transactions
    Reporting->>Reporting: Generate camt.052<br/>(Intraday report)
    Reporting->>Reporting: Generate camt.053<br/>(EOD statement)
    Reporting->>Reporting: Generate camt.054<br/>(Notifications)
    Reporting->>Database: INSERT reports
    Reporting->>Kafka: PublishEvent<br/>ReportingGenerated
    deactivate Reporting

    Note over Database: Audit trail created
    Database->>Database: INSERT audit_events<br/>(all operations)

    rect rgb(0, 255, 0)
    Note over Client,Reporting: Payment lifecycle complete<br/>All services updated
    end
```

**Timeline**: ~500ms total (async processing)

**Key Points**:
- ✅ Correlation ID propagated through all events
- ✅ Each service processes independently
- ✅ Audit trail created for every operation
- ✅ Risk score calculated but payment proceeds (low risk)
- ✅ Reports generated from updated ledger
- ✅ Core chain is explicit: `payment.initiated → settlement.completed → ledger.updated → reporting-service`

---

## Scenario 2: High-Risk Transaction Detection

Shows the flow when anomaly detection flags a suspicious transaction.

```mermaid
sequenceDiagram
    participant Client
    participant Orchestrator as Payment<br/>Orchestrator
    participant Kafka
    participant Anomaly as Anomaly<br/>Detection
    participant Monitor as Resilience<br/>Monitor
    participant Database as PostgreSQL
    participant Operator as Operator<br/>Backoffice

    Client->>Orchestrator: POST /payments<br/>{large amount, new beneficiary}

    activate Orchestrator
    Orchestrator->>Database: INSERT payment<br/>(status=INITIATED)
    Orchestrator->>Kafka: PublishEvent<br/>PaymentInitiated
    deactivate Orchestrator

    Orchestrator-->>Client: 201 Created<br/>{paymentId, status}

    activate Anomaly
    Kafka->>Anomaly: consume PaymentInitiated
    Anomaly->>Database: SELECT beneficiary_history
    Anomaly->>Database: SELECT account_transactions

    rect rgb(255, 200, 0)
    Note over Anomaly: Risk Assessment
    Anomaly->>Anomaly: ✓ High transaction amount (+30 points)<br/>✓ New beneficiary (+25 points)<br/>✓ Rapid payment frequency (+20 points)<br/>= Risk Score: 75/100
    end

    alt Risk Score >= Threshold (75)
        Anomaly->>Kafka: PublishEvent<br/>AnomalyDetected<br/>{riskScore: 75, reasons: [...]}
        Anomaly->>Database: INSERT anomaly_alert
    else
        Anomaly->>Kafka: PublishEvent<br/>AnomalyScored<br/>{riskScore: low}
    end
    deactivate Anomaly

    activate Monitor
    Kafka->>Monitor: consume AnomalyDetected
    Monitor->>Database: INSERT incident<br/>(status=OPEN)
    Monitor->>Database: INSERT audit_events<br/>(incident created)
    Monitor->>Kafka: PublishEvent<br/>IncidentCreated
    deactivate Monitor

    Note over Database: Audit trail with full context

    rect rgb(255, 100, 100)
    Note over Monitor,Operator: Operational Alert<br/>Operator review required
    end

    Operator->>Operator: Receives alert<br/>(via backoffice dashboard)
    Operator->>Database: GET /backoffice/payments/{id}/details
    Operator->>Database: GET /backoffice/payments/{id}/audit-trail

    alt Operator Action: Approve
        Operator->>Database: UPDATE payment<br/>(status=APPROVED)
        Operator->>Database: INSERT audit_events<br/>(manual approval)
    else Operator Action: Block
        Operator->>Database: UPDATE payment<br/>(status=BLOCKED)
        Operator->>Database: INSERT audit_events<br/>(manual block)
        Operator->>Kafka: PublishEvent<br/>PaymentBlocked
    end

    rect rgb(0, 255, 0)
    Note over Operator,Database: Manual review complete<br/>Audit evidence recorded
    end
```

**Key Points**:
- ✅ Anomaly detected by rule-based scoring
- ✅ Incident created automatically
- ✅ Operator alerted for manual review
- ✅ All actions audited with correlation ID
- ✅ Multiple risk factors documented
- ✅ Payment can proceed or be blocked based on review

---

## Scenario 3: Settlement Failure with Retry and Recovery

Shows how the system handles transient failures and implements recovery.

```mermaid
sequenceDiagram
    participant Settlement as Settlement<br/>Service
    participant Kafka
    participant MockClearing as Mock Clearing<br/>(unavailable)
    participant Monitor as Resilience<br/>Monitor
    participant DLQ as Dead Letter<br/>Topic
    participant Database as PostgreSQL
    participant Operator as Operator

    activate Settlement
    Kafka->>Settlement: consume PaymentInitiated<br/>(Attempt 1)
    Settlement->>MockClearing: POST /settle {payment}

    Note over Settlement,MockClearing: Timeout - Transient Failure
    MockClearing--xSettlement: Connection refused

    rect rgb(255, 200, 0)
    Note over Settlement: Exponential Backoff Retry
    Settlement->>Settlement: Schedule retry<br/>after 1 second
    end
    deactivate Settlement

    break Waiting 1 second...
    end

    activate Settlement
    Kafka->>Settlement: Retry attempt 2<br/>(from offset)
    Settlement->>MockClearing: POST /settle {payment}

    Note over Settlement,MockClearing: Service recovering...
    MockClearing--xSettlement: Connection timeout

    Settlement->>Settlement: Schedule retry<br/>after 2 seconds
    deactivate Settlement

    break Waiting 2 seconds...
    end

    activate Settlement
    Kafka->>Settlement: Retry attempt 3
    Settlement->>MockClearing: POST /settle {payment}

    Note over Settlement,MockClearing: Service recovered!
    MockClearing-->>Settlement: 200 OK {settlementId: S123}

    Settlement->>Database: INSERT settlement_record
    Settlement->>Kafka: PublishEvent<br/>SettlementCompleted
    deactivate Settlement

    rect rgb(0, 255, 0)
    Note over Kafka,Database: Payment continues through<br/>ledger and reporting flow
    end

    rect rgb(100, 100, 100)
    Note over Settlement,Monitor: Alternate Path: Permanent Failure

    Settlement->>Settlement: Retry 5 times fail
    Settlement->>DLQ: Move message to DLQ<br/>payment.initiated.dlq
    Settlement->>Database: UPDATE payment<br/>(status=FAILED)
    Settlement->>Kafka: PublishEvent<br/>SettlementFailed

    Monitor->>DLQ: monitor DLQ size
    Monitor->>Database: INSERT incident<br/>(status=DLQ_OVERFLOW)
    Monitor->>Operator: Alert: Messages in DLQ

    Operator->>Database: GET /backoffice/dlq/messages
    Operator->>Database: Investigate failure
    Operator->>Database: POST /backoffice/replay<br/>{topic: payment.initiated, dlq: true}

    Note over DLQ,Kafka: Messages replayed to original topic<br/>for reprocessing
    end
```

**Timeline**:
- Success path: ~1.5-4 seconds (with retries)
- Failure path: Minutes (manual intervention)

**Key Points**:
- ✅ Automatic retry with exponential backoff (1s, 2s, 4s, 8s, 16s)
- ✅ Transient failures handled gracefully
- ✅ Messages survive service restarts
- ✅ Permanent failures moved to DLQ
- ✅ Operator can replay from DLQ
- ✅ All retry attempts audited
- ✅ Correlation ID maintained throughout

---

## Event State Transitions

### Payment Lifecycle States

```mermaid
stateDiagram-v2
    [*] --> INITIATED

    INITIATED --> VALIDATED: PaymentValidated
    INITIATED --> FAILED: ValidationError

    VALIDATED --> SETTLED: SettlementCompleted
    VALIDATED --> SETTLEMENT_FAILED: SettlementFailed

    SETTLED --> REPORTED: ReportGenerated
    SETTLED --> HIGH_RISK: AnomalyDetected

    HIGH_RISK --> APPROVED: ManualApproval
    HIGH_RISK --> BLOCKED: ManualBlock

    APPROVED --> REPORTED: ReportGenerated

    SETTLEMENT_FAILED --> RETRY: ExponentialBackoff
    RETRY --> SETTLED: RetrySuccess
    RETRY --> DLQ: MaxRetriesExceeded

    DLQ --> RETRY: ManualReplay

    BLOCKED --> [*]
    REPORTED --> [*]
    FAILED --> [*]
```

### Incident Lifecycle States

```mermaid
stateDiagram-v2
    [*] --> OPEN

    OPEN --> INVESTIGATING: OperatorReview
    OPEN --> ACKNOWLEDGED: OperatorAck

    INVESTIGATING --> RESOLVED: CauseFixed
    INVESTIGATING --> ESCALATED: Critical

    ACKNOWLEDGED --> RESOLVED: Manual
    ACKNOWLEDGED --> ESCALATED: Critical

    ESCALATED --> [*]
    RESOLVED --> [*]
```

---

## Correlation ID Flow Example

All related events share the same correlation ID:

```
Payment Request
  ↓ (UUID generated: 550e8400-e29b-41d4-a716-446655440000)
  ├─ audit_events.correlation_id = 550e8400...
  ├─ payment.initiated event header
  ├─ payment record
  └─ All downstream operations inherit same ID
      ├─ Settlement event
      ├─ Ledger event
      ├─ Anomaly event
      ├─ Reporting event
      └─ All audit entries linked

Query: SELECT * FROM audit_events
       WHERE correlation_id = '550e8400...'
Result: Complete transaction timeline
```

---

## Related Documentation

- **Arc42 Section 6**: Runtime View
- **ADR-006**: Immutable Audit Trail
- **ADR-007**: Correlation ID Strategy
- **ADR-008**: Retry and Dead Letter Queue
- **Container Diagram**: See [02-container-diagram.md](02-container-diagram.md)
- **Deployment Diagram**: See [04-deployment-diagram.md](04-deployment-diagram.md)

---

**Last Updated**: June 26, 2026

