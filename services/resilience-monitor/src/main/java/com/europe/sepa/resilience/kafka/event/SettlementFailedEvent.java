package com.europe.sepa.resilience.kafka.event;

import java.time.Instant;

/**
 * Event representing a failed settlement transaction.
 * Consumed from settlement.failed topic.
 */
public class SettlementFailedEvent {

    private String eventId;
    private String eventType;
    private String correlationId;
    private Instant timestamp;
    private String paymentId;
    private String orderer;
    private String beneficiary;
    private double amount;
    private String currency;
    private String failureReason;
    private int retryCount;

    public SettlementFailedEvent() {
    }

    public SettlementFailedEvent(String eventId, String eventType, String correlationId,
                                  Instant timestamp, String paymentId, String orderer,
                                  String beneficiary, double amount, String currency,
                                  String failureReason, int retryCount) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
        this.paymentId = paymentId;
        this.orderer = orderer;
        this.beneficiary = beneficiary;
        this.amount = amount;
        this.currency = currency;
        this.failureReason = failureReason;
        this.retryCount = retryCount;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderer() { return orderer; }
    public void setOrderer(String orderer) { this.orderer = orderer; }

    public String getBeneficiary() { return beneficiary; }
    public void setBeneficiary(String beneficiary) { this.beneficiary = beneficiary; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    @Override
    public String toString() {
        return "SettlementFailedEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", failureReason='" + failureReason + '\'' +
                ", retryCount=" + retryCount +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}

