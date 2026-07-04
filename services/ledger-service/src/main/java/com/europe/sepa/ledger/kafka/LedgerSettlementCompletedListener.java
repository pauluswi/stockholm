package com.europe.sepa.ledger.kafka;

import com.europe.sepa.ledger.kafka.event.SettlementCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * First real Kafka consumer in the ledger service.
 */
@Component
public class LedgerSettlementCompletedListener {

    private static final Logger log = LoggerFactory.getLogger(LedgerSettlementCompletedListener.class);

    private final LedgerService ledgerService;

    public LedgerSettlementCompletedListener(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @KafkaListener(
            topics = "${stockholm.kafka.topics.settlement-completed}",
            containerFactory = "settlementCompletedKafkaListenerContainerFactory"
    )
    public void onSettlementCompleted(SettlementCompletedEvent event) {
        log.info("Received settlement completed event: paymentId={}, settlementId={}, correlationId={}",
                event.getPaymentId(), event.getSettlementId(), event.getCorrelationId());
        ledgerService.updateLedger(event);
    }
}

