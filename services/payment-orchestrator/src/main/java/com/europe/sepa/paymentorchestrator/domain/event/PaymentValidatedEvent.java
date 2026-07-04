package com.europe.sepa.paymentorchestrator.domain.event;

/**
 * Published when a payment passes validation.
 * Indicates payment is syntactically correct and meets business rules.
 */
public class PaymentValidatedEvent extends DomainEvent {
    
    private final String paymentId;
    private final String validationStatus;
    private final String validationMessage;
    
    public PaymentValidatedEvent(String correlationId, String paymentId, 
                                 String validationStatus, String validationMessage) {
        super(correlationId);
        this.paymentId = paymentId;
        this.validationStatus = validationStatus;
        this.validationMessage = validationMessage;
    }
    
    public String getPaymentId() { return paymentId; }
    public String getValidationStatus() { return validationStatus; }
    public String getValidationMessage() { return validationMessage; }
    
    @Override
    public String toString() {
        return "PaymentValidatedEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", validationStatus='" + validationStatus + '\'' +
                ", correlationId='" + getCorrelationId() + '\'' +
                '}';
    }
}

