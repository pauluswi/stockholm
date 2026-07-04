package com.europe.sepa.anomaly.kafka;

import com.europe.sepa.anomaly.kafka.event.PaymentInitiatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that consumes payment.initiated events.
 * This is the entry point for anomaly detection in the event chain.
 */
@Component
public class PaymentInitiatedListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentInitiatedListener.class);

    private final AnomalyDetectionService anomalyDetectionService;

    public PaymentInitiatedListener(AnomalyDetectionService anomalyDetectionService) {
        this.anomalyDetectionService = anomalyDetectionService;
    }

    @KafkaListener(
            topics = "${stockholm.kafka.topics.payment-initiated}",
            containerFactory = "paymentInitiatedKafkaListenerContainerFactory"
    )
    public void onPaymentInitiated(PaymentInitiatedEvent event) {
        log.debug("[anomaly] onPaymentInitiated: {}", event);
        anomalyDetectionService.detectAnomalies(event);
    }
}

