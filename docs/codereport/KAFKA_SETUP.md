# Embedded Kafka Setup for Stockholm

## Overview

The Stockholm payment orchestrator uses **embedded Kafka** for local development and testing. This allows developers to:

- ✅ Develop locally without external Kafka infrastructure
- ✅ Run integration tests with realistic event publishing
- ✅ Test event flow and inter-service communication
- ✅ Simulate payment workflows end-to-end

## Architecture

### Event Model

All events inherit from `DomainEvent` and include:
- **eventId**: Unique identifier (UUID)
- **eventType**: Class name of the event
- **timestamp**: Creation time (Instant)
- **correlationId**: For distributed tracing across services

### Events in Stockholm

| Event | Topic | Published By | Consumed By |
|-------|-------|--------------|-------------|
| PaymentInitiatedEvent | `payment.initiated` | Payment Orchestrator | Settlement, Anomaly Detection |
| PaymentValidatedEvent | `payment.validated` | Validation Service | Settlement Service |
| SettlementCompletedEvent | `settlement.completed` | Settlement Service | Ledger Service |
| AnomalyDetectedEvent | `anomaly.detected` | Anomaly Detection | Resilience Monitor |
| LedgerUpdatedEvent | `ledger.updated` | Ledger Service | Reporting Service |
| ReportGeneratedEvent | `report.generated` | Reporting Service | Backoffice API |

## Local Development Setup

### 1. Start Infrastructure

For local development, you have two options:

#### Option A: Using Docker Compose (Recommended)
```bash
cd /Users/slametwidodo/IdeaProjects/stockholm
docker compose up -d
```

This starts:
- Kafka broker on `localhost:9092`
- Zookeeper on `localhost:2181`
- PostgreSQL on `localhost:5432`
- Redis on `localhost:6379`

#### Option B: Using Embedded Kafka
The tests automatically use embedded Kafka:
```bash
mvn test
```

### 2. Run Payment Orchestrator

```bash
cd services/payment-orchestrator
mvn spring-boot:run
```

Service will start on `http://localhost:8081`

### 3. Create a Payment (Publish Event)

```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderer": "BANK001",
    "beneficiary": "CUST456",
    "amount": 1000.50,
    "currency": "EUR"
  }'
```

Response:
```json
{
  "paymentId": "PAY-a1b2c3d4-e5f6-7890-abcd",
  "status": "initiated",
  "correlationId": "corr-xyz123"
}
```

**What happens:**
1. REST endpoint receives payment request
2. Generates paymentId and correlationId
3. Creates `PaymentInitiatedEvent`
4. **Publishes event to `payment.initiated` topic**
5. Returns 201 Created

### 4. Verify Event Published

Monitor Kafka topics:

```bash
# List all topics
docker exec -it $(docker ps | grep kafka | awk '{print $1}') \
  kafka-topics --list --bootstrap-server localhost:9092

# Monitor events in a topic
docker exec -it $(docker ps | grep kafka | awk '{print $1}') \
  kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic payment.initiated --from-beginning
```

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test
```bash
# Kafka event publishing test
mvn test -Dtest=EventPublisherIntegrationTest

# REST API test
mvn test -Dtest=PaymentControllerIntegrationTest
```

### Test Features

#### 1. **EventPublisherIntegrationTest**
Tests:
- Event creation and serialization
- Publishing to Kafka topics
- Event properties (correlation ID, event ID, timestamp)

Uses: **Embedded Kafka**

#### 2. **PaymentControllerIntegrationTest**
Tests:
- REST endpoint receives payment requests
- Validation of required fields
- Default currency assignment
- Event publishing triggered

Uses: **MockMvc + Embedded Kafka**

## Configuration

### Kafka Properties (application.properties)

```properties
# Bootstrap server
spring.kafka.bootstrap-servers=localhost:9092

# Producer settings
spring.kafka.producer.key-serializer=...StringSerializer
spring.kafka.producer.value-serializer=...JsonSerializer
spring.kafka.producer.acks=all                    # Wait for all replicas
spring.kafka.producer.retries=3                   # Retry failed sends
spring.kafka.producer.properties.enable.idempotence=true

# Consumer settings
spring.kafka.consumer.group-id=stockholm-consumer-group
spring.kafka.consumer.auto-offset-reset=earliest  # Start from beginning
spring.kafka.consumer.max-poll-records=100

# Topic names (configurable)
stockholm.kafka.topics.payment-initiated=payment.initiated
stockholm.kafka.topics.payment-validated=payment.validated
stockholm.kafka.topics.settlement-completed=settlement.completed
stockholm.kafka.topics.settlement-failed=settlement.failed
stockholm.kafka.topics.anomaly-detected=anomaly.detected
stockholm.kafka.topics.ledger-updated=ledger.updated
stockholm.kafka.topics.report-generated=report.generated
```

### Customizing Topics

Edit `KafkaTopicProperties`:
```java
@Component
@ConfigurationProperties(prefix = "stockholm.kafka.topics")
public class KafkaTopicProperties {
    private String paymentInitiated = "payment.initiated";  // Can override via application.properties
    // ...
}
```

Or via application.properties:
```properties
stockholm.kafka.topics.payment-initiated=my.custom.topic
```

## Publishing Events Programmatically

### Using EventPublisher Service

```java
@Service
public class PaymentService {

    private final EventPublisher eventPublisher;

    public void processPayment(PaymentRequest request) {
        // ... business logic ...

        PaymentInitiatedEvent event = new PaymentInitiatedEvent(
            correlationId,
            paymentId,
            request.getOrderer(),
            request.getBeneficiary(),
            request.getAmount(),
            request.getCurrency()
        );

        // Publish to Kafka
        eventPublisher.publish(event);
    }
}
```

### Event Routing

`EventPublisher` automatically routes events to correct topics:

```java
private String getTopicForEvent(DomainEvent event) {
    return switch (event.getClass().getSimpleName()) {
        case "PaymentInitiatedEvent" -> topicProperties.getPaymentInitiated();
        case "PaymentValidatedEvent" -> topicProperties.getPaymentValidated();
        // ... etc
    };
}
```

## Consuming Events in Other Services

### Implement Kafka Listener

In Settlement Service or other services:

```java
@Component
public class PaymentEventListener {

    @KafkaListener(topics = "${stockholm.kafka.topics.payment-initiated}",
                   groupId = "settlement-service-group")
    public void onPaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Received PaymentInitiatedEvent: {}", event.getPaymentId());

        // Process settlement for this payment
        settlementService.settle(event);
    }
}
```

## Troubleshooting

### Issue: "Cannot connect to Kafka"

**Solution 1:** Start Docker Compose infrastructure
```bash
docker compose up -d
```

**Solution 2:** Verify Kafka is running
```bash
docker ps | grep kafka
```

### Issue: "Topic does not exist"

**Solution:** Kafka will auto-create topics on first publish.

Check topic creation:
```bash
docker exec -it $(docker ps | grep kafka | awk '{print $1}') \
  kafka-topics --list --bootstrap-server localhost:9092
```

### Issue: Events not being published

**Check logs:**
```bash
# In payment-orchestrator logs, look for:
# "Publishing event: type=PaymentInitiatedEvent..."
# "Event published successfully..."
```

**Enable debug logging:**
```properties
logging.level.com.europe.sepa.paymentorchestrator=DEBUG
```

### Issue: Tests fail with Kafka errors

**Solution:** Embedded Kafka needs to be cleaned between tests

Try:
```bash
mvn clean test
```

## Demo Scenarios

### Scenario 1: Happy Path Payment

```bash
# 1. Start services
mvn spring-boot:run

# 2. Create payment
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{"orderer":"BANK001","beneficiary":"CUST001","amount":500,"currency":"EUR"}'

# 3. Observe events in Kafka
# payment.initiated event published
# → Settlement service picks it up
# → Publishes settlement.completed
# → Ledger service picks it up
# → Publishes ledger.updated
```

### Scenario 2: Testing with Anomaly Detection

```bash
# 1. Send high-risk payment (large amount, new beneficiary)
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderer":"BANK001",
    "beneficiary":"NEWCUST999",
    "amount":999999,
    "currency":"EUR"
  }'

# 2. Anomaly detection service detects high risk
# 3. Publishes anomaly.detected event
# 4. Resilience monitor creates incident
```

## Next Steps

1. **Implement Event Consumers** in Settlement, Ledger, Reporting services
2. **Add Persistence** for events (Event Store)
3. **Implement Retries** for failed event processing (Dead Letter Queue)
4. **Add Observability** with correlation IDs and distributed tracing
5. **Create Mock External Services** that consume and respond to events

## References

- [Spring Kafka Documentation](https://spring.io/projects/spring-kafka)
- [Kafka Architecture](/docs/architecture/adr/ADR-003-kafka-event-backbone.md)
- [Event-Driven Design](/docs/architecture/adr/ADR-002-event-driven-architecture.md)
- [Retry & DLQ Patterns](/docs/architecture/adr/ADR-008-retry-and-dead-letter-queue.md)

---

**Last Updated:** July 4, 2026
**Status:** Implementation Complete - Local Development Ready

