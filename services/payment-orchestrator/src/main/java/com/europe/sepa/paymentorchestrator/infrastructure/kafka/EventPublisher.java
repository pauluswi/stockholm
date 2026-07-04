package com.europe.sepa.paymentorchestrator.infrastructure.kafka;

import com.europe.sepa.paymentorchestrator.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Service to publish domain events to Kafka topics.
 * Handles serialization and routing of events.
 */
@Service
public class EventPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;
    
    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate, 
                         KafkaTopicProperties topicProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicProperties = topicProperties;
    }
    
    /**
     * Publish a domain event to the appropriate Kafka topic based on event type.
     * 
     * @param event The domain event to publish
     */
    public void publish(DomainEvent event) {
        String topicName = getTopicForEvent(event);
        String key = event.getCorrelationId();
        
        Message<DomainEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader("kafka_messageKey", key)
                .setHeader("X-Correlation-ID", event.getCorrelationId())
                .setHeader("X-Event-ID", event.getEventId())
                .setHeader("X-Event-Type", event.getEventType())
                .build();
        
        log.info("Publishing event: type={}, correlationId={}, topic={}", 
                 event.getEventType(), event.getCorrelationId(), topicName);
        
        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event: type={}, correlationId={}", 
                                 event.getEventType(), event.getCorrelationId(), ex);
                    } else {
                        log.debug("Event published successfully: type={}, correlationId={}, offset={}", 
                                 event.getEventType(), event.getCorrelationId(), 
                                 result.getRecordMetadata().offset());
                    }
                });
    }
    
    /**
     * Determine the target topic for a given event type.
     */
    private String getTopicForEvent(DomainEvent event) {
        return switch (event.getClass().getSimpleName()) {
            case "PaymentInitiatedEvent" -> topicProperties.getPaymentInitiated();
            case "PaymentValidatedEvent" -> topicProperties.getPaymentValidated();
            case "SettlementCompletedEvent" -> topicProperties.getSettlementCompleted();
            case "AnomalyDetectedEvent" -> topicProperties.getAnomalyDetected();
            case "LedgerUpdatedEvent" -> topicProperties.getLedgerUpdated();
            case "ReportGeneratedEvent" -> topicProperties.getReportGenerated();
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
        };
    }
}

