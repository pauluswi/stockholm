package com.europe.sepa.anomaly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Stores anomaly detection results for auditing and analysis.
 */
@Entity
@Table(name = "anomaly_scores")
public class AnomalyScore {

    @Id
    private String id;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private int riskScore;

    @Column(nullable = false)
    private String severity;

    @Column(length = 2000)
    private String riskFactors;

    @Column
    private String correlationId;

    @Column
    private Instant eventTimestamp;

    @Column(nullable = false)
    private Instant scoredAt;

    @Column
    private boolean flagged;

    public AnomalyScore() {
    }

    public AnomalyScore(String id, String paymentId, int riskScore, String severity,
                        String riskFactors, String correlationId, Instant eventTimestamp, Instant scoredAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.riskScore = riskScore;
        this.severity = severity;
        this.riskFactors = riskFactors;
        this.correlationId = correlationId;
        this.eventTimestamp = eventTimestamp;
        this.scoredAt = scoredAt;
        this.flagged = riskScore >= 75;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public Instant getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(Instant eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public Instant getScoredAt() { return scoredAt; }
    public void setScoredAt(Instant scoredAt) { this.scoredAt = scoredAt; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
}

