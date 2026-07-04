# Anomaly Detection Service - Implementation Complete

## Overview
The **Anomaly Detection Service** has been fully built as the Priority 1 high-value component of the SEPA Payment Orchestration system.

## Architecture
- **Consumes:** `payment.initiated` events (from Payment Orchestrator in parallel with Settlement/Ledger)
- **Implements:** Rule-based risk scoring engine
- **Publishes:** `anomaly.detected` events (when risk score >= 75)
- **Stores:** Anomaly alerts in PostgreSQL for audit trail
- **Port:** 8087

## Key Components

### 1. Event DTOs (No Cross-Service Dependencies)
- `PaymentInitiatedEvent` - Mirrors payment.initiated from orchestrator
- `AnomalyDetectedEvent` - Published when anomalies flagged

### 2. Risk Scoring Engine (`RiskScoringEngine.java`)
Explainable rule-based scoring (0-100 scale):

**Risk Factors:**
- **High Amount Risk (0-30 points)**
  - Very High (> €50,000): +30
  - High (> €10,000): +20
  - Moderate (> €5,000): +10

- **New Beneficiary (0-25 points)**
  - First payment to unknown party: +25

- **Rapid Payment Frequency (0-20 points)**
  - 5+ payments in 24h: +20
  - 3+ payments in 24h: +10

- **Unusual Time (0-15 points)**
  - Outside business hours or weekend: +15

- **Round Amount (0-10 points)**
  - Suspiciously round numbers: +10

**Severity Levels:**
- HIGH: Score >= 75 → Flags anomaly, publishes event
- MEDIUM: Score >= 50
- LOW: Score < 50

### 3. Kafka Integration
- **Consumer:** `payment-initiated-listener` subscribes to `payment.initiated`
- **Producer:** Publishes to `anomaly.detected` when threshold exceeded
- **Consumer Group:** `anomaly-detection-service-group`
- **Configuration:** Auto-creation of topics, JSON serialization

### 4. Persistence Layer
- **Entity:** `AnomalyScore` JPA entity stores all scoring results
- **Repository:** `AnomalyScoreRepository` for queries
- **Table:** `anomaly_scores` with correlation ID tracking for audit trail

### 5. REST API
- `GET /anomalies/by-payment/{paymentId}` - Query score for payment
- `GET /anomalies/flagged` - List all flagged (HIGH risk) anomalies
- `GET /anomalies/severity/{severity}` - Filter by severity level
- `GET /health` - Health check endpoint

## Parallel Event Processing

The service demonstrates parallel event processing:
- Listens for `payment.initiated` events independently
- Processes scoring concurrently (does not block orchestrator)
- Stores results in DB for downstream systems (reporting, compliance)
- Publishes anomaly events for immediate action (fraud prevention)

## Build Status
```
✅ All 12 source files compiled successfully
✅ All 3 unit tests passing
✅ Complete JAR package built: anomaly-detection-service-3.2.3.jar
✅ Ready for Docker containerization and deployment
```

## Testing
- **Unit Tests:** `RiskScoringEngineTest` (3 tests)
  - Very high amount transaction scoring
  - Low amount transaction scoring
  - Combined high-risk factors

## Configuration
```properties
# Server
server.port=8087
spring.application.name=anomaly-detection

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
stockholm.kafka.topics.payment-initiated=payment.initiated
stockholm.kafka.topics.anomaly-detected=anomaly.detected
stockholm.kafka.create-topics=true

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/stockholm
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

## File Structure
```
services/anomaly-detection-service/
├── src/main/java/com/europe/sepa/anomaly/
│   ├── AnomalyDetectionApplication.java          [Entry point]
│   ├── kafka/
│   │   ├── AnomalyKafkaConfiguration.java        [Kafka setup]
│   │   ├── AnomalyKafkaProperties.java           [Topic config]
│   │   ├── AnomalyDetectionService.java          [Orchestration]
│   │   ├── PaymentInitiatedListener.java         [Event consumer]
│   │   ├── event/
│   │   │   ├── PaymentInitiatedEvent.java        [DTO]
│   │   │   └── AnomalyDetectedEvent.java         [DTO]
│   ├── entity/
│   │   └── AnomalyScore.java                     [JPA entity]
│   ├── repository/
│   │   └── AnomalyScoreRepository.java           [Data access]
│   ├── scoring/
│   │   └── RiskScoringEngine.java                [Scoring logic]
│   └── web/
│       ├── AnomalyController.java                [REST API]
│       └── HealthController.java                 [Health check]
└── pom.xml                                        [Maven config]
```

## Dependencies Added
- spring-kafka
- spring-boot-starter-data-jpa
- postgresql
- org.apache.kafka:kafka-clients
- awaitility (testing)
- h2 (in-memory DB for testing)

## Next Steps
1. Run with Docker Compose: `docker-compose up anomaly-detection-service`
2. Verify Kafka connectivity: `curl http://localhost:8087/health`
3. Monitor anomalies: `curl http://localhost:8087/anomalies/flagged`
4. Integration with Payment Orchestrator and Settlement Service for end-to-end flow

