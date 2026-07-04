package com.europe.sepa.ledger.kafka;

import com.europe.sepa.ledger.entity.LedgerEntry;
import com.europe.sepa.ledger.kafka.event.LedgerUpdatedEvent;
import com.europe.sepa.ledger.kafka.event.SettlementCompletedEvent;
import com.europe.sepa.ledger.repository.LedgerEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Persists ledger entries and emits ledger updates.
 */
@Service
public class LedgerService {

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);

    private final LedgerEntryRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final LedgerKafkaProperties topicProperties;

    public LedgerService(LedgerEntryRepository repository,
                         KafkaTemplate<String, Object> kafkaTemplate,
                         LedgerKafkaProperties topicProperties) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.topicProperties = topicProperties;
    }

    public LedgerUpdatedEvent updateLedger(SettlementCompletedEvent event) {
        LedgerEntry entry = new LedgerEntry(
                UUID.randomUUID().toString(),
                event.getPaymentId(),
                event.getSettlementId(),
                event.getStatus(),
                event.getCorrelationId(),
                event.getEventId(),
                event.getEventType(),
                event.getTimestamp(),
                Instant.now()
        );

        repository.save(entry);

        LedgerUpdatedEvent updatedEvent = new LedgerUpdatedEvent(
                entry.getId(),
                entry.getPaymentId(),
                entry.getSettlementId(),
                "ledger-updated",
                entry.getCorrelationId(),
                UUID.randomUUID().toString(),
                "LedgerUpdatedEvent",
                Instant.now()
        );

        kafkaTemplate.send(topicProperties.getLedgerUpdated(), entry.getCorrelationId(), updatedEvent);
        log.info("Ledger updated: ledgerEntryId={}, paymentId={}, correlationId={}",
                entry.getId(), entry.getPaymentId(), entry.getCorrelationId());

        return updatedEvent;
    }
}

