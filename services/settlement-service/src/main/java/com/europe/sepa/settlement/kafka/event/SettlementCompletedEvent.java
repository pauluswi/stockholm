package com.europe.sepa.settlement.kafka.event;

import java.time.Instant;

/**
 * Event published after a payment has been settled.
 */
public class SettlementCompletedEvent {

    private String paymentId;
    private String settlementId;
    private String status;
    private String correlationId;
    private String eventId;
    private String eventType;
    private Instant timestamp;

    public SettlementCompletedEvent() {
    }

    public SettlementCompletedEvent(String paymentId, String settlementId, String status,
                                    String correlationId, String eventId, String eventType,
                                    Instant timestamp) {
        this.paymentId = paymentId;
        this.settlementId = settlementId;
        this.status = status;
        this.correlationId = correlationId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

