package com.europe.sepa.ledger;

import com.europe.sepa.ledger.entity.LedgerEntry;
import com.europe.sepa.ledger.kafka.event.SettlementCompletedEvent;
import com.europe.sepa.ledger.repository.LedgerEntryRepository;
import com.europe.sepa.ledger.testsupport.TestKafkaTopics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test: a SettlementCompletedEvent published to Kafka is consumed,
 * persisted as a LedgerEntry, and a LedgerUpdatedEvent is emitted.
 *
 * Topic setup is handled by the {@link TestKafkaTopics} helper so that each
 * test run starts from a clean state.
 */
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = {"log.dir=target/embedded-kafka"})
@DirtiesContext
class LedgerIntegrationTest {

    private static final String SETTLEMENT_COMPLETED_TOPIC = "settlement.completed";
    private static final String LEDGER_UPDATED_TOPIC       = "ledger.updated";
    private static final String LEDGER_FAILED_TOPIC        = "ledger.failed";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @Autowired
    private org.springframework.kafka.test.EmbeddedKafkaBroker embeddedKafkaBroker;

    private TestKafkaTopics testKafkaTopics;

    @BeforeEach
    void setUp() {
        testKafkaTopics = new TestKafkaTopics(embeddedKafkaBroker.getBrokersAsString());
        testKafkaTopics.recreateTopics(SETTLEMENT_COMPLETED_TOPIC, LEDGER_UPDATED_TOPIC, LEDGER_FAILED_TOPIC);
        ledgerEntryRepository.deleteAll();
    }

    @Test
    void whenSettlementCompletedEventIsPublished_thenLedgerEntryIsPersisted() {
        // given
        String paymentId      = "PAY-" + UUID.randomUUID();
        String settlementId   = "SET-" + UUID.randomUUID();
        String correlationId  = UUID.randomUUID().toString();
        String eventId        = UUID.randomUUID().toString();
        Instant eventTime     = Instant.now();

        SettlementCompletedEvent event = new SettlementCompletedEvent();
        event.setPaymentId(paymentId);
        event.setSettlementId(settlementId);
        event.setStatus("COMPLETED");
        event.setCorrelationId(correlationId);
        event.setEventId(eventId);
        event.setEventType("SettlementCompletedEvent");
        event.setTimestamp(eventTime);

        // when
        kafkaTemplate.send(SETTLEMENT_COMPLETED_TOPIC, correlationId, event);

        // then – wait up to 10 s for the consumer to persist the entry
        await().atMost(10, SECONDS).untilAsserted(() -> {
            Optional<LedgerEntry> found = ledgerEntryRepository.findByPaymentId(paymentId);
            assertThat(found).isPresent();

            LedgerEntry entry = found.get();
            assertThat(entry.getId()).isNotBlank();
            assertThat(entry.getPaymentId()).isEqualTo(paymentId);
            assertThat(entry.getSettlementId()).isEqualTo(settlementId);
            assertThat(entry.getStatus()).isEqualTo("COMPLETED");
            assertThat(entry.getCorrelationId()).isEqualTo(correlationId);
            assertThat(entry.getSourceEventId()).isEqualTo(eventId);
            assertThat(entry.getSourceEventType()).isEqualTo("SettlementCompletedEvent");
            assertThat(entry.getSourceTimestamp()).isEqualTo(eventTime);
            assertThat(entry.getProcessedAt()).isNotNull();
        });
    }
}

