package com.europe.sepa.paymentorchestrator.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events.
 * All domain events are immutable and represent facts that have occurred.
 */
public abstract class DomainEvent {
    
    private final String eventId = UUID.randomUUID().toString();
    private final String eventType = this.getClass().getSimpleName();
    private final Instant timestamp = Instant.now();
    private final String correlationId;
    
    protected DomainEvent(String correlationId) {
        this.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    @Override
    public String toString() {
        return "DomainEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}

