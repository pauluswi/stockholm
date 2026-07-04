package com.europe.sepa.reporting.kafka;

import com.europe.sepa.reporting.entity.PaymentReport;
import com.europe.sepa.reporting.kafka.event.LedgerUpdatedEvent;
import com.europe.sepa.reporting.repository.PaymentReportRepository;
import com.europe.sepa.reporting.testsupport.TestKafkaTopics;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test for the reporting-service Kafka consumer.
 *
 * Flow under test:
 *   test publishes LedgerUpdatedEvent → ledger.updated topic
 *   → LedgerUpdatedListener → ReportingService → PaymentReport persisted
 *   → assert row exists in payment_reports table
 */
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class LedgerUpdatedListenerIntegrationTest {

    private static final String TOPIC = "ledger.updated";

    @Value("${spring.embedded.kafka.brokers}")
    private String brokers;

    @Autowired
    private PaymentReportRepository reportRepository;

    private TestKafkaTopics kafkaTopics;
    private KafkaTemplate<String, LedgerUpdatedEvent> producer;

    @BeforeAll
    void setUp() {
        kafkaTopics = new TestKafkaTopics(brokers);
        kafkaTopics.recreateTopics(TOPIC);

        Map<String, Object> producerProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                JsonSerializer.ADD_TYPE_INFO_HEADERS, false
        );
        producer = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
    }

    @BeforeEach
    void cleanDatabase() {
        reportRepository.deleteAll();
    }

    @AfterAll
    void tearDown() {
        kafkaTopics.deleteTopics(TOPIC);
    }

    @Test
    void whenLedgerUpdatedEventPublished_thenPaymentReportIsPersisted() throws Exception {
        // Arrange
        String ledgerEntryId = UUID.randomUUID().toString();
        String paymentId     = UUID.randomUUID().toString();
        String settlementId  = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();

        LedgerUpdatedEvent event = new LedgerUpdatedEvent(
                ledgerEntryId,
                paymentId,
                settlementId,
                "SETTLED",
                correlationId,
                UUID.randomUUID().toString(),
                "LedgerUpdatedEvent",
                Instant.now()
        );

        // Act
        producer.send(TOPIC, correlationId, event).get(5, TimeUnit.SECONDS);

        // Assert — wait up to 10 s for the listener to process and persist
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<PaymentReport> reports = reportRepository.findByPaymentId(paymentId);
                    assertThat(reports).hasSize(1);

                    PaymentReport report = reports.get(0);
                    assertThat(report.getId()).isEqualTo(ledgerEntryId);
                    assertThat(report.getPaymentId()).isEqualTo(paymentId);
                    assertThat(report.getSettlementId()).isEqualTo(settlementId);
                    assertThat(report.getStatus()).isEqualTo("SETTLED");
                    assertThat(report.getCorrelationId()).isEqualTo(correlationId);
                    assertThat(report.getReportedAt()).isNotNull();
                });
    }

    @Test
    void whenSameLedgerEntryReceivedTwice_thenOnlyOneReportExists() throws Exception {
        // Arrange
        String ledgerEntryId = UUID.randomUUID().toString();
        String paymentId     = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();

        LedgerUpdatedEvent event = new LedgerUpdatedEvent(
                ledgerEntryId, paymentId, "s-1", "SETTLED", correlationId,
                UUID.randomUUID().toString(), "LedgerUpdatedEvent", Instant.now()
        );

        // Act — send the same event twice (idempotency test)
        producer.send(TOPIC, correlationId, event).get(5, TimeUnit.SECONDS);
        producer.send(TOPIC, correlationId, event).get(5, TimeUnit.SECONDS);

        // Assert — still only one row (upsert semantics)
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<PaymentReport> reports = reportRepository.findByPaymentId(paymentId);
                    assertThat(reports).hasSize(1);
                });
    }
}

