package com.europe.sepa.anomaly.kafka;

import com.europe.sepa.anomaly.entity.AnomalyScore;
import com.europe.sepa.anomaly.kafka.event.AnomalyDetectedEvent;
import com.europe.sepa.anomaly.kafka.event.PaymentInitiatedEvent;
import com.europe.sepa.anomaly.repository.AnomalyScoreRepository;
import com.europe.sepa.anomaly.scoring.RiskScoringEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Orchestrates anomaly detection: scores payments and publishes alerts if flagged.
 */
@Service
public class AnomalyDetectionService {

    private static final Logger log = LoggerFactory.getLogger(AnomalyDetectionService.class);

    private final RiskScoringEngine scoringEngine;
    private final AnomalyScoreRepository scoreRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AnomalyKafkaProperties topicProperties;

    public AnomalyDetectionService(RiskScoringEngine scoringEngine,
                                   AnomalyScoreRepository scoreRepository,
                                   KafkaTemplate<String, Object> kafkaTemplate,
                                   AnomalyKafkaProperties topicProperties) {
        this.scoringEngine = scoringEngine;
        this.scoreRepository = scoreRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topicProperties = topicProperties;
    }

    @Transactional
    public void detectAnomalies(PaymentInitiatedEvent event) {
        log.info("[anomaly] Scoring payment {} amount={}", event.getPaymentId(), event.getAmount());

        // 1. Score the payment
        RiskScoringEngine.RiskScoreResult result = scoringEngine.scorePayment(
                event.getPaymentId(),
                event.getBeneficiary(),
                event.getAmount(),
                event.getTimestamp(),
                event.getCorrelationId()
        );

        // 2. Persist the score
        AnomalyScore score = new AnomalyScore(
                UUID.randomUUID().toString(),
                event.getPaymentId(),
                result.score,
                result.severity,
                String.join(", ", result.reasons),
                event.getCorrelationId(),
                event.getTimestamp(),
                Instant.now()
        );
        scoreRepository.save(score);

        // 3. If flagged, publish anomaly.detected event
        if (result.isFlagged()) {
            publishAnomalyDetected(event, result);
        } else {
            log.info("[anomaly] Payment {} passed scoring (score={})", 
                     event.getPaymentId(), result.score);
        }
    }

    private void publishAnomalyDetected(PaymentInitiatedEvent event,
                                         RiskScoringEngine.RiskScoreResult result) {
        AnomalyDetectedEvent anomalyEvent = new AnomalyDetectedEvent(
                UUID.randomUUID().toString(),
                "AnomalyDetectedEvent",
                event.getCorrelationId(),
                Instant.now(),
                event.getPaymentId(),
                result.score,
                result.reasons,
                result.severity
        );

        Message<Object> message = MessageBuilder
                .withPayload((Object) anomalyEvent)
                .setHeader(KafkaHeaders.TOPIC, topicProperties.getAnomalyDetected())
                .setHeader("kafka_messageKey", event.getCorrelationId())
                .setHeader("X-Correlation-ID", event.getCorrelationId())
                .build();

        kafkaTemplate.send(message).whenComplete((result2, ex) -> {
            if (ex != null) {
                log.error("[anomaly] Failed to publish anomaly.detected for payment {}",
                         event.getPaymentId(), ex);
            } else {
                log.info("[anomaly] Published anomaly.detected for payment {} score={}",
                        event.getPaymentId(), result.score);
            }
        });
    }
}

