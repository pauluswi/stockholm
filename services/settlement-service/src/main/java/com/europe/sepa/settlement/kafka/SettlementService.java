package com.europe.sepa.settlement.kafka;

import com.europe.sepa.settlement.kafka.event.PaymentInitiatedEvent;
import com.europe.sepa.settlement.kafka.event.SettlementCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Core settlement use case.
 */
@Service
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SettlementKafkaProperties topicProperties;

    public SettlementService(KafkaTemplate<String, Object> kafkaTemplate, SettlementKafkaProperties topicProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicProperties = topicProperties;
    }

    public SettlementCompletedEvent settle(PaymentInitiatedEvent payment) {
        String settlementId = "SETTLEMENT-" + UUID.randomUUID();
        SettlementCompletedEvent completed = new SettlementCompletedEvent(
                payment.getPaymentId(),
                settlementId,
                "settled",
                payment.getCorrelationId(),
                UUID.randomUUID().toString(),
                "SettlementCompletedEvent",
                Instant.now()
        );

        log.info("Settlement completed: paymentId={}, settlementId={}, correlationId={}",
                payment.getPaymentId(), settlementId, payment.getCorrelationId());

        kafkaTemplate.send(topicProperties.getSettlementCompleted(), payment.getCorrelationId(), completed);
        return completed;
    }
}

