package com.europe.sepa.paymentorchestrator.domain.event;

import java.math.BigDecimal;

/**
 * Published when a payment is initiated by the client.
 * Triggers orchestration of the entire payment processing flow.
 */
public class PaymentInitiatedEvent extends DomainEvent {
    
    private final String paymentId;
    private final String orderer;
    private final String beneficiary;
    private final BigDecimal amount;
    private final String currency;
    
    public PaymentInitiatedEvent(String correlationId, String paymentId, 
                                  String orderer, String beneficiary, 
                                  BigDecimal amount, String currency) {
        super(correlationId);
        this.paymentId = paymentId;
        this.orderer = orderer;
        this.beneficiary = beneficiary;
        this.amount = amount;
        this.currency = currency;
    }
    
    // Getters for JSON serialization
    public String getPaymentId() { return paymentId; }
    public String getOrderer() { return orderer; }
    public String getBeneficiary() { return beneficiary; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    
    @Override
    public String toString() {
        return "PaymentInitiatedEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", orderer='" + orderer + '\'' +
                ", beneficiary='" + beneficiary + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", eventId='" + getEventId() + '\'' +
                ", correlationId='" + getCorrelationId() + '\'' +
                '}';
    }
}

