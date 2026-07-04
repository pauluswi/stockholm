package com.europe.sepa.paymentorchestrator.domain.event;

/**
 * Published when settlement is completed successfully.
 * Indicates funds have been transferred and are no longer reversible.
 */
public class SettlementCompletedEvent extends DomainEvent {
    
    private final String paymentId;
    private final String settlementId;
    private final String status;
    
    public SettlementCompletedEvent(String correlationId, String paymentId, 
                                    String settlementId, String status) {
        super(correlationId);
        this.paymentId = paymentId;
        this.settlementId = settlementId;
        this.status = status;
    }
    
    public String getPaymentId() { return paymentId; }
    public String getSettlementId() { return settlementId; }
    public String getStatus() { return status; }
    
    @Override
    public String toString() {
        return "SettlementCompletedEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", settlementId='" + settlementId + '\'' +
                ", status='" + status + '\'' +
                ", correlationId='" + getCorrelationId() + '\'' +
                '}';
    }
}

