package com.europe.sepa.backoffice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "incidents")
public class IncidentView {

    @Id
    private Long id;

    @Column(nullable = false)
    private String incidentId;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String incidentType;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String status;

    private Integer riskScore;
    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant acknowledgedAt;
    private Instant resolvedAt;
    private String assignedTo;

    public Long getId() {
        return id;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public String getAssignedTo() {
        return assignedTo;
    }
}

