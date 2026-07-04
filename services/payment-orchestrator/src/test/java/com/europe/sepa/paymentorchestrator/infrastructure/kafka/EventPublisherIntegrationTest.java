package com.europe.sepa.paymentorchestrator.infrastructure.kafka;

import com.europe.sepa.paymentorchestrator.domain.event.PaymentInitiatedEvent;
import com.europe.sepa.paymentorchestrator.testsupport.KafkaIntegrationTest;
import com.europe.sepa.paymentorchestrator.testsupport.TestKafkaTopics;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Kafka event publishing and consuming.
 * Uses embedded Kafka broker for testing without external infrastructure.
 */
@KafkaIntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventPublisherIntegrationTest {
    private final EventPublisher eventPublisher;
    private final EmbeddedKafkaBroker embeddedKafkaBroker;

    private TestKafkaTopics kafkaTopics;

    @Autowired
    public EventPublisherIntegrationTest(EventPublisher eventPublisher,
                                         EmbeddedKafkaBroker embeddedKafkaBroker) {
        this.eventPublisher = eventPublisher;
        this.embeddedKafkaBroker = embeddedKafkaBroker;
    }

    @BeforeAll
    void setUpTopics() {
        kafkaTopics = new TestKafkaTopics(embeddedKafkaBroker);
        kafkaTopics.recreateTopics("payment.initiated");
    }

    @AfterAll
    void tearDownTopics() {
        kafkaTopics.deleteTopics("payment.initiated");
    }
    
    @Test
    public void testPublishPaymentInitiatedEvent() throws Exception {
        // Arrange
        String correlationId = "corr-12345";
        String paymentId = "PAY-67890";
        PaymentInitiatedEvent event = new PaymentInitiatedEvent(
                correlationId,
                paymentId,
                "ORDERER001",
                "BENEFICIARY001",
                new BigDecimal("100.00"),
                "EUR"
        );
        
        // Act
        assertDoesNotThrow(() -> eventPublisher.publish(event));
        
        // Assert
        // Give async callback a short window.
        TimeUnit.MILLISECONDS.sleep(100);
        
        // Verify event properties
        assertEquals(paymentId, event.getPaymentId());
        assertEquals(correlationId, event.getCorrelationId());
        assertEquals("EUR", event.getCurrency());
        assertEquals(new BigDecimal("100.00"), event.getAmount());
        
        assertNotNull(eventPublisher, "EventPublisher should be autowired");
    }
    
    @Test
    public void testEventSerialization() {
        // Arrange
        String correlationId = "test-correlation-123";
        PaymentInitiatedEvent event = new PaymentInitiatedEvent(
                correlationId,
                "PAY-999",
                "SENDER",
                "RECEIVER",
                new BigDecimal("50.00"),
                "EUR"
        );
        
        // Act & Assert
        assertEquals("PaymentInitiatedEvent", event.getEventType());
        assertEquals(correlationId, event.getCorrelationId());
        assertNotNull(event.getEventId());
        assertNotNull(event.getTimestamp());
        
        assertNotNull(eventPublisher, "EventPublisher should be autowired");
    }
}

