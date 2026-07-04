package com.europe.sepa.settlement.kafka;

import com.europe.sepa.settlement.kafka.event.PaymentInitiatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * First real Kafka consumer in the settlement service.
 */
@Component
public class SettlementPaymentInitiatedListener {

    private static final Logger log = LoggerFactory.getLogger(SettlementPaymentInitiatedListener.class);

    private final SettlementService settlementService;

    public SettlementPaymentInitiatedListener(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @KafkaListener(
            topics = "${stockholm.kafka.topics.payment-initiated}",
            containerFactory = "paymentInitiatedKafkaListenerContainerFactory"
    )
    public void onPaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Received payment initiated event: paymentId={}, correlationId={}, amount={}",
                event.getPaymentId(), event.getCorrelationId(), event.getAmount());
        settlementService.settle(event);
    }
}

