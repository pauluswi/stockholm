package com.europe.sepa.backoffice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "anomaly_scores")
public class AnomalyScoreView {

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

    private String correlationId;

    private Instant eventTimestamp;

    @Column(nullable = false)
    private Instant scoredAt;

    private boolean flagged;

    public String getId() {
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public String getSeverity() {
        return severity;
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public Instant getScoredAt() {
        return scoredAt;
    }

    public boolean isFlagged() {
        return flagged;
    }
}

