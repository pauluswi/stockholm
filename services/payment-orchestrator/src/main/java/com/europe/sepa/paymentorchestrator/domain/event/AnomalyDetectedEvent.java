package com.europe.sepa.paymentorchestrator.domain.event;

/**
 * Published when an anomaly is detected in a transaction.
 * Triggers incident creation and manual review workflows.
 */
public class AnomalyDetectedEvent extends DomainEvent {
    
    private final String paymentId;
    private final int riskScore;
    private final String riskLevel;
    private final String reason;
    
    public AnomalyDetectedEvent(String correlationId, String paymentId, 
                               int riskScore, String riskLevel, String reason) {
        super(correlationId);
        this.paymentId = paymentId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.reason = reason;
    }
    
    public String getPaymentId() { return paymentId; }
    public int getRiskScore() { return riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public String getReason() { return reason; }
    
    @Override
    public String toString() {
        return "AnomalyDetectedEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", riskScore=" + riskScore +
                ", riskLevel='" + riskLevel + '\'' +
                ", reason='" + reason + '\'' +
                ", correlationId='" + getCorrelationId() + '\'' +
                '}';
    }
}

