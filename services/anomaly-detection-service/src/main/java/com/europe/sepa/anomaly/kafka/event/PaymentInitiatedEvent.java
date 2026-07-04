package com.europe.sepa.anomaly.kafka.event;

import java.time.Instant;

/**
 * Mirror DTO of payment.initiated event.
 * No cross-service compile dependency.
 */
public class PaymentInitiatedEvent {

    private String eventId;
    private String eventType;
    private String correlationId;
    private Instant timestamp;
    private String paymentId;
    private String orderer;
    private String beneficiary;
    private double amount;
    private String currency;

    public PaymentInitiatedEvent() {
    }

    public PaymentInitiatedEvent(String eventId, String eventType, String correlationId,
                                  Instant timestamp, String paymentId, String orderer,
                                  String beneficiary, double amount, String currency) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
        this.paymentId = paymentId;
        this.orderer = orderer;
        this.beneficiary = beneficiary;
        this.amount = amount;
        this.currency = currency;
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

    @Override
    public String toString() {
        return "PaymentInitiatedEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", orderer='" + orderer + '\'' +
                ", beneficiary='" + beneficiary + '\'' +
                ", amount=" + amount +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}

