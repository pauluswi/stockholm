package com.europe.sepa.reporting.kafka;

import com.europe.sepa.reporting.kafka.event.LedgerUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that consumes {@code ledger.updated} events.
 * This is the entry point of the reporting-service in the event chain:
 * payment.initiated → settlement.completed → ledger.updated → [reporting-service]
 */
@Component
public class LedgerUpdatedListener {

    private static final Logger log = LoggerFactory.getLogger(LedgerUpdatedListener.class);

    private final ReportingService reportingService;

    public LedgerUpdatedListener(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @KafkaListener(
            topics = "${stockholm.kafka.topics.ledger-updated}",
            containerFactory = "ledgerUpdatedKafkaListenerContainerFactory"
    )
    public void onLedgerUpdated(LedgerUpdatedEvent event) {
        log.debug("[reporting] onLedgerUpdated: {}", event);
        reportingService.handleLedgerUpdated(event);
    }
}

