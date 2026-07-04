package com.europe.sepa.anomaly.kafka.event;

import java.time.Instant;
import java.util.List;

/**
 * Event published when anomaly scoring exceeds threshold.
 */
public class AnomalyDetectedEvent {

    private String eventId;
    private String eventType;
    private String correlationId;
    private Instant timestamp;
    private String paymentId;
    private int riskScore;
    private List<String> reasons;
    private String severity;

    public AnomalyDetectedEvent() {
    }

    public AnomalyDetectedEvent(String eventId, String eventType, String correlationId,
                                 Instant timestamp, String paymentId, int riskScore,
                                 List<String> reasons, String severity) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
        this.paymentId = paymentId;
        this.riskScore = riskScore;
        this.reasons = reasons;
        this.severity = severity;
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

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    @Override
    public String toString() {
        return "AnomalyDetectedEvent{" +
                "paymentId='" + paymentId + '\'' +
                ", riskScore=" + riskScore +
                ", severity='" + severity + '\'' +
                ", reasons=" + reasons +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}

