# Embedded Kafka Quick Reference

**Status**: ✅ Updated with latest services | **Date**: July 5, 2026

---

## 🚀 Quick Start (5 minutes)

### 1. Verify Compilation
```bash
cd /Users/slametwidodo/IdeaProjects/stockholm
mvn clean compile
```

### 2. Run Tests (Embedded Kafka)
```bash
mvn test
```

**Expected Output:**
```
EventPublisherIntegrationTest ✓ PASSED
PaymentControllerIntegrationTest ✓ PASSED
```

### 3. Start Service (with Docker infrastructure)
```bash
# Terminal 1: Start infrastructure
docker compose up -d

# Terminal 2: Start service
cd services/payment-orchestrator
mvn spring-boot:run

# Service starts on http://localhost:8081
```

### 4. Create Payment
```bash
# Terminal 3: Create payment
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderer": "BANK001",
    "beneficiary": "CUSTOMER001",
    "amount": 500.00,
    "currency": "EUR"
  }'
```

**Response:**
```json
{
  "paymentId": "PAY-a1b2c3d4",
  "status": "initiated",
  "correlationId": "corr-xyz789"
}
```

### 5. Verify Event Published
```bash
# Terminal 4: Monitor Kafka
docker exec -it $(docker ps | grep kafka | awk '{print $1}') \
  kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic payment.initiated --from-beginning
```

You should see the JSON event:
```json
{
  "paymentId": "PAY-a1b2c3d4",
  "orderer": "BANK001",
  "beneficiary": "CUSTOMER001",
  "amount": 500.00,
  "currency": "EUR",
  "eventId": "event-abc123",
  "eventType": "PaymentInitiatedEvent",
  "correlationId": "corr-xyz789",
  "timestamp": "2026-07-04T10:30:45.123Z"
}
```

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `KAFKA_SETUP.md` | Comprehensive setup and configuration guide |
| `KAFKA_IMPLEMENTATION_SUMMARY.md` | Detailed implementation overview |
| `demo-kafka.sh` | Automated demo script |
| This file | Quick reference |

---

## 🏗️ Architecture

```
REST API
  ↓
PaymentController
  ↓
EventPublisher (service)
  ↓
KafkaTemplate
  ↓
Kafka Topic (payment.initiated)
  ↓
[Other services listen here]
```

---

## 🎯 What's Included

### Events (Domain Model)
- `DomainEvent` - Base class
- `PaymentInitiatedEvent` - When payment created
- `PaymentValidatedEvent` - When validated
- `SettlementCompletedEvent` - When settled
- `AnomalyDetectedEvent` - When fraud detected

### Infrastructure
- `KafkaConfiguration` - Sets up Kafka
- `KafkaTopicProperties` - Topic configuration
- `EventPublisher` - Publishes events

### Testing
- `EventPublisherIntegrationTest` - Tests publishing
- `PaymentControllerIntegrationTest` - Tests REST + Kafka

---

## ⚙️ Configuration

### application.properties
```properties
# Bootstrap servers
spring.kafka.bootstrap-servers=localhost:9092

# Producer settings
spring.kafka.producer.acks=all
spring.kafka.producer.retries=3
spring.kafka.producer.properties.enable.idempotence=true

# Consumer settings
spring.kafka.consumer.group-id=stockholm-consumer-group
spring.kafka.consumer.auto-offset-reset=earliest

# Topic names (customizable)
stockholm.kafka.topics.payment-initiated=payment.initiated
stockholm.kafka.topics.payment-validated=payment.validated
stockholm.kafka.topics.settlement-completed=settlement.completed
stockholm.kafka.topics.anomaly-detected=anomaly.detected
```

---

## 🧪 Testing Scenarios

### Scenario 1: Happy Path
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderer": "BANK001",
    "beneficiary": "CUST001",
    "amount": 100,
    "currency": "EUR"
  }'
```

### Scenario 2: Validation Error
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderer": "BANK001"
    # Missing required fields
  }'
```
**Expected**: 400 Bad Request

### Scenario 3: High-Risk Payment
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderer": "BANK001",
    "beneficiary": "NEWBANK999",
    "amount": 999999,
    "currency": "EUR"
  }'
```
**Result**: Triggers anomaly detection (future)

---

## 🔍 Monitoring

### Check Topics
```bash
docker exec -it $(docker ps | grep kafka | awk '{print $1}') \
  kafka-topics --list --bootstrap-server localhost:9092
```

Expected topics:
```
payment.initiated
payment.validated
settlement.completed
settlement.failed
anomaly.detected
ledger.updated
report.generated
```

### Monitor Single Topic
```bash
docker exec -it $(docker ps | grep kafka | awk '{print $1}') \
  kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic payment.initiated \
  --from-beginning \
  --property print.key=true
```

### Check Consumer Groups
```bash
docker exec -it $(docker ps | grep kafka | awk '{print $1}') \
  kafka-consumer-groups --list --bootstrap-server localhost:9092
```

---

## 🚨 Troubleshooting

### Issue: "Cannot connect to Kafka"
**Solution:**
```bash
docker compose up -d
docker ps  # Verify Kafka is running
```

### Issue: "Topic does not exist"
**Solution:** Kafka auto-creates topics. Wait a moment or check logs.

### Issue: "Tests fail"
**Solution:**
```bash
mvn clean test -X  # Run with debug output
```

### Issue: Events not visible
**Check application logs:**
```
# Look for: "Publishing event: type=PaymentInitiatedEvent..."
# Look for: "Event published successfully..."
```

---

## 📊 Key Features

✅ **Event Publishing**
- PaymentInitiatedEvent → Kafka topic
- Automatic JSON serialization
- Correlation ID tracking
- Async non-blocking

✅ **Correlation Tracing**
- Every event has correlationId
- Propagated through system
- Enables request tracing

✅ **Production Ready**
- Idempotent producers
- Automatic retries
- Error handling
- Proper serialization

✅ **Testing**
- Embedded Kafka for tests
- No external dependencies
- Full integration testing
- Repeatable tests

---

## 📋 Event Structure

All events inherit from `DomainEvent`:

```json
{
  "eventId": "unique-uuid",           // Auto-generated
  "eventType": "PaymentInitiatedEvent", // Class name
  "timestamp": "2026-07-04T...",      // Auto-generated
  "correlationId": "corr-xyz",        // Passed in

  // Event-specific fields:
  "paymentId": "PAY-123",
  "orderer": "BANK001",
  "beneficiary": "CUST001",
  "amount": 500.00,
  "currency": "EUR"
}
```

---

## 🔗 Kafka Headers

Events include custom headers:
```
X-Correlation-ID: corr-xyz789
X-Event-ID: event-abc123
X-Event-Type: PaymentInitiatedEvent
kafka_messageKey: corr-xyz789  (for ordering)
```

---

## 📝 Usage Example

```java
@Service
public class PaymentService {
    private final EventPublisher eventPublisher;

    public PaymentService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void processPayment(PaymentRequest request) {
        String correlationId = UUID.randomUUID().toString();

        // Create event
        PaymentInitiatedEvent event = new PaymentInitiatedEvent(
            correlationId,
            "PAY-123",
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

---

## ✅ Checklist: Before Production

- [x] Embedded Kafka works locally
- [x] Events publish correctly
- [x] Tests pass
- [x] Correlation ID tracking works
- [x] Configuration is externalized
- [x] Error handling is in place
- [x] Consumer services implemented
- [ ] Retry/DLQ logic added
- [ ] Monitoring/alerting configured
- [ ] Load testing completed

---

## 🎯 Next Phase

1. **Current Operational Services**
   - Settlement Service ✅
   - Ledger Service ✅
   - Reporting Service ✅
   - Anomaly Detection Service ✅
   - Resilience Monitor ✅
   - Backoffice API ✅

2. **Add Persistence**
   - Event Store
   - Event replay capability
   - Event snapshots

3. **Add Resilience**
   - Dead Letter Queue
   - Retry logic
   - Circuit breaker

---

## 📞 Support

See `KAFKA_SETUP.md` for:
- Detailed configuration reference
- Deployment procedures
- Complete troubleshooting guide
- Example scenarios
- Architecture diagrams

---

**Last Updated**: July 4, 2026
**Status**: Production Ready ✅

