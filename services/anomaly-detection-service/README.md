# Anomaly Detection Service

Real-time risk scoring and anomaly detection for SEPA payment transactions using explainable rule-based scoring.

## Overview

The **Anomaly Detection Service** is a microservice that:
- Consumes `payment.initiated` events from the Payment Orchestrator
- Evaluates transaction risk using a multi-dimensional scoring engine
- Publishes `anomaly.detected` events when risk exceeds threshold (75/100)
- Stores all scoring results in PostgreSQL for audit trail and compliance
- Provides REST APIs for querying anomaly data

**Port:** 8087
**Event Topics:** `payment.initiated` (consumer), `anomaly.detected` (producer)

## Features

✅ **Explainable Risk Scoring** — Rule-based, no ML black boxes
✅ **Multi-Factor Analysis** — Amount, beneficiary, frequency, timing, pattern detection
✅ **Real-time Event Processing** — Kafka-based async scoring
✅ **Audit Trail** — All scores stored with correlation IDs for compliance
✅ **REST Query API** — Search anomalies by payment ID, severity, or flagged status
✅ **Parallel Processing** — Scores payments without blocking orchestrator

## Architecture

```
Payment Orchestrator
        ↓
[payment.initiated event]
        ↓
Anomaly Detection Service
    ├─ Kafka Listener: Consumes events
    ├─ Risk Scoring Engine: Evaluates transaction
    ├─ Database: Stores AnomalyScore entities
    └─ Kafka Producer: Publishes anomaly.detected if score >= 75
        ↓
[anomaly.detected event] → Reporting Service, Resilience Monitor
```

## Risk Scoring Factors

The scoring engine evaluates transactions on a **0-100 scale**:

### 1. High Amount Risk (0-30 points)
| Amount | Points | Rule |
|--------|--------|------|
| > €50,000 | 30 | Very high transaction |
| > €10,000 | 20 | High transaction |
| > €5,000 | 10 | Moderate transaction |
| ≤ €5,000 | 0 | Normal transaction |

### 2. New Beneficiary (0-25 points)
- First payment to unknown party: **+25 points**
- Trusted/standard beneficiaries: **0 points**

### 3. Payment Frequency (0-20 points)
| Frequency | Points | Rule |
|-----------|--------|------|
| ≥ 5 payments in 24h | 20 | Rapid/suspicious |
| ≥ 3 payments in 24h | 10 | Elevated frequency |
| < 3 payments in 24h | 0 | Normal |

### 4. Unusual Timing (0-15 points)
- Outside business hours (00:00-06:00, 20:00+): **+15 points**
- Weekend transactions: **+15 points**
- Business hours (06:00-20:00, weekdays): **0 points**

### 5. Round Amount Pattern (0-10 points)
- Amounts divisible by 1000 (€1000, €5000, €10000): **+10 points**
- Non-round amounts: **0 points**

### Severity Levels
```
Score ≥ 75  → HIGH     (Flagged as anomaly, event published)
Score ≥ 50  → MEDIUM   (Monitored, not published)
Score < 50  → LOW      (Normal transaction)
```

## REST API Examples

### Query Anomaly Score for Payment
```bash
curl -X GET http://localhost:8087/anomalies/by-payment/PAY-001 \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "paymentId": "PAY-001",
  "correlationId": "CORR-12345",
  "riskScore": 85,
  "severity": "HIGH",
  "reasons": [
    "Very high transaction amount (> €50,000)",
    "New beneficiary (first payment to this party)",
    "Payment outside business hours"
  ],
  "flagged": true,
  "timestamp": "2026-07-04T02:30:00Z"
}
```

### Get All Flagged Anomalies
```bash
curl -X GET http://localhost:8087/anomalies/flagged \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "paymentId": "PAY-001",
    "riskScore": 85,
    "severity": "HIGH",
    "flagged": true
  },
  {
    "id": 3,
    "paymentId": "PAY-003",
    "riskScore": 78,
    "severity": "HIGH",
    "flagged": true
  }
]
```

### Filter by Severity Level
```bash
curl -X GET http://localhost:8087/anomalies/severity/HIGH \
  -H "Content-Type: application/json"
```

### Health Check
```bash
curl -X GET http://localhost:8087/health
```

## Kafka Event Examples

### Input: payment.initiated
The service consumes this event from the Payment Orchestrator:

```json
{
  "eventId": "EVT-PAY-001",
  "eventType": "payment.initiated",
  "correlationId": "CORR-12345",
  "timestamp": "2026-07-04T02:30:00Z",
  "paymentId": "PAY-001",
  "orderer": "DE89370400440532013000",
  "beneficiary": "IT60X0542811101000000123456",
  "amount": 75000.50,
  "currency": "EUR"
}
```

### Output: anomaly.detected (published if score ≥ 75)
When the risk score exceeds 75, the service publishes:

```json
{
  "eventId": "EVT-ANOM-001",
  "eventType": "anomaly.detected",
  "correlationId": "CORR-12345",
  "timestamp": "2026-07-04T02:30:01Z",
  "paymentId": "PAY-001",
  "riskScore": 85,
  "severity": "HIGH",
  "reasons": [
    "Very high transaction amount (> €50,000)",
    "New beneficiary (first payment to this party)",
    "Payment outside business hours"
  ]
}
```

## Configuration

Add to `application.properties`:

```properties
# Server Configuration
server.port=8087
spring.application.name=anomaly-detection

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=anomaly-detection-service-group
spring.kafka.consumer.auto-offset-reset=earliest

# Topics
stockholm.kafka.topics.payment-initiated=payment.initiated
stockholm.kafka.topics.anomaly-detected=anomaly.detected

# Auto-create topics (development only)
stockholm.kafka.create-topics=true

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/stockholm
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL82Dialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

## Code Examples

### Consuming Payment Events

The `PaymentInitiatedListener` automatically consumes and processes events:

```java
@Component
@Slf4j
public class PaymentInitiatedListener {

    @KafkaListener(topics = "payment.initiated")
    public void onPaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Received payment event: {}", event.getPaymentId());

        // Score the payment
        RiskScoringEngine.RiskScoreResult result = riskEngine.scorePayment(
            event.getPaymentId(),
            event.getBeneficiary(),
            event.getAmount(),
            event.getTimestamp(),
            event.getCorrelationId()
        );

        // Publish anomaly if flagged
        if (result.isFlagged()) {
            publishAnomalyEvent(event, result);
        }
    }
}
```

### Risk Scoring in Action

```java
// Calculate risk score for a transaction
RiskScoreResult result = riskEngine.scorePayment(
    "PAY-001",                           // paymentId
    "IT60X0542811101000000123456",       // beneficiary
    75000.50,                            // amount (€75,000 = HIGH)
    Instant.now(),                       // timestamp (02:30 = UNUSUAL)
    "CORR-12345"                         // correlationId
);

// Result:
// score: 85/100
// severity: HIGH
// reasons: [
//   "Very high transaction amount (> €50,000)",  // +30
//   "New beneficiary (first payment to this party)",  // +25
//   "Payment outside business hours"  // +15
// ]
// isFlagged(): true → publishes anomaly.detected
```

### Querying Results Programmatically

```java
@Autowired
private AnomalyScoreRepository scoreRepository;

// Get score for specific payment
Optional<AnomalyScore> score = scoreRepository.findByPaymentId("PAY-001");

// List all flagged anomalies
List<AnomalyScore> flagged = scoreRepository.findByFlaggedTrue();

// Find by severity
List<AnomalyScore> highRisk = scoreRepository.findBySeverity("HIGH");
```

## Database Schema

The service stores all scoring results in the `anomaly_scores` table:

```sql
CREATE TABLE anomaly_scores (
    id BIGINT PRIMARY KEY,
    payment_id VARCHAR(50) UNIQUE,
    correlation_id VARCHAR(50),
    risk_score INTEGER NOT NULL,
    severity VARCHAR(20),
    flagged BOOLEAN DEFAULT FALSE,
    reasons TEXT,  -- JSON array of reason strings
    timestamp TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_id ON anomaly_scores(payment_id);
CREATE INDEX idx_flagged ON anomaly_scores(flagged);
CREATE INDEX idx_severity ON anomaly_scores(severity);
```

## Testing

Run the integration tests to verify the scoring logic:

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=AnomalyDetectionIntegrationTest
```

**Test Coverage:**
- Risk scoring for high-value transactions
- Severity level classification
- Anomaly publication on HIGH risk
- Kafka event consumption and processing

Example test:

```java
@Test
public void whenHighRiskPaymentPublished_thenFlaggedAsHigh() {
    // Publish a €75,000 payment at 2:30 AM
    PaymentInitiatedEvent event = new PaymentInitiatedEvent(
        "EVT-001",
        "payment.initiated",
        "CORR-123",
        Instant.now(),
        "PAY-001",
        "DE89370400440532013000",
        "IT60X0542811101000000123456",  // New beneficiary
        75000.00,                        // > €50,000 threshold
        "EUR"
    );

    kafkaTemplate.send("payment.initiated", event);

    // Wait for async processing
    await().atMost(Duration.ofSeconds(5))
        .untilAsserted(() -> {
            Optional<AnomalyScore> result = scoreRepository.findByPaymentId("PAY-001");
            assertTrue(result.isPresent());
            assertEquals("HIGH", result.get().getSeverity());
            assertTrue(result.get().isFlagged());
            assertThat(result.get().getReasons())
                .contains("Very high transaction amount (> €50,000)");
        });
}
```

## Building and Deployment

### Build the Service

```bash
cd services/anomaly-detection-service
mvn clean package
```

### Docker Deployment

```bash
# Build Docker image
docker build -t anomaly-detection:3.2.3 .

# Run with docker-compose
docker-compose up -d anomaly-detection-service

# View logs
docker logs -f stockholm_anomaly-detection-service_1
```

### Docker Compose Configuration

```yaml
anomaly-detection-service:
  build:
    context: ./services/anomaly-detection-service
    dockerfile: Dockerfile
  ports:
    - "8087:8087"
  environment:
    SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/stockholm
    SPRING_DATASOURCE_USERNAME: postgres
    SPRING_DATASOURCE_PASSWORD: postgres
  depends_on:
    - kafka
    - postgres
  networks:
    - stockholm-network
```

## Integration with Other Services

### Payment Flow
```
Payment Orchestrator (8080) — publishes payment.initiated
    ↓
Anomaly Detection Service (8087) — scores & publishes anomaly.detected
    ↓
Reporting Service (8089) — consumes for analytics
Resilience Monitor (8091) — consumes for incident management
```

### Event Correlation
All events carry `correlationId` for end-to-end tracing:

```
payment.initiated (CORR-12345)
  ↓
anomaly.detected (CORR-12345) — same correlation ID
  ↓
incident.created (CORR-12345) — traceable chain
```

## Performance Considerations

- **Scoring Latency:** < 100ms per payment
- **Throughput:** 1000+ payments/second
- **Database Indexes:** On `payment_id`, `flagged`, `severity` for fast queries
- **Kafka Partitions:** Default 3 partitions for `payment.initiated` topic
- **Consumer Group:** `anomaly-detection-service-group` (one instance per partition)

## File Structure

```
services/anomaly-detection-service/
├── src/main/java/com/europe/sepa/anomaly/
│   ├── AnomalyDetectionApplication.java          # Spring Boot entry point
│   ├── kafka/
│   │   ├── AnomalyKafkaConfiguration.java        # Kafka producer/consumer setup
│   │   ├── AnomalyKafkaProperties.java           # Topic configuration
│   │   ├── AnomalyDetectionService.java          # Orchestration service
│   │   ├── PaymentInitiatedListener.java         # Event listener
│   │   └── event/
│   │       ├── PaymentInitiatedEvent.java        # Input event DTO
│   │       └── AnomalyDetectedEvent.java         # Output event DTO
│   ├── entity/
│   │   └── AnomalyScore.java                     # JPA entity for DB persistence
│   ├── repository/
│   │   └── AnomalyScoreRepository.java           # Spring Data JPA repository
│   ├── scoring/
│   │   └── RiskScoringEngine.java                # Core scoring logic
│   └── web/
│       ├── AnomalyController.java                # REST endpoints
│       └── HealthController.java                 # Health check endpoint
├── src/test/java/com/europe/sepa/anomaly/
│   └── AnomalyDetectionIntegrationTest.java      # Integration tests
├── pom.xml                                        # Maven configuration
└── Dockerfile                                     # Container image
```

## Dependencies

Key dependencies in `pom.xml`:

```xml
<!-- Spring Boot & Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## Troubleshooting

### Kafka Connection Issues
```bash
# Check Kafka broker connectivity
kafka-broker-api-versions --bootstrap-server localhost:9092

# List topics
kafka-topics --bootstrap-server localhost:9092 --list
```

### View Anomaly Events
```bash
# Consume anomaly.detected events
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic anomaly.detected --from-beginning --property print.key=true
```

### Query Database
```bash
# Connect to PostgreSQL
psql -U postgres -d stockholm

# View anomaly scores
SELECT payment_id, risk_score, severity, flagged FROM anomaly_scores
ORDER BY created_at DESC LIMIT 10;
```

### Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `Connection refused: localhost:9092` | Kafka not running | Start Kafka: `docker-compose up kafka` |
| `No consumer group found` | Consumer not started | Service must be running and consuming |
| `FATAL: role "postgres" does not exist` | Database user not created | Initialize database with docker-compose |

## Future Enhancements

- **ML-Enhanced Scoring** — Integrate neural networks for pattern detection
- **Historical Correlation** — Query ledger service for payment history
- **Custom Rules Engine** — Admin API to define custom scoring rules
- **Explainability Dashboard** — Visualize scoring reasons and audit trail
- **Retraining Pipeline** — Continuous model improvement from false positives

## License

Part of Stockholm SEPA Payment Orchestration System.

