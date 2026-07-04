package com.europe.sepa.resilience.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Incident entity representing an operational incident.
 * 
 * Incidents are created when:
 * - Anomalies are detected in transactions
 * - Settlement failures occur
 * - DLQ overflow is detected
 */
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String incidentId;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String incidentType;  // ANOMALY_DETECTED, SETTLEMENT_FAILED, DLQ_OVERFLOW

    @Column(nullable = false)
    private String severity;  // LOW, MEDIUM, HIGH, CRITICAL

    @Column(nullable = false)
    private String status;  // OPEN, ACKNOWLEDGED, RESOLVED, ESCALATED

    private Integer riskScore;
    private String failureReason;

    @ElementCollection
    @CollectionTable(name = "incident_details", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "detail")
    private List<String> details = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt;

    private Instant acknowledgedAt;
    private Instant resolvedAt;

    private String assignedTo;

    public Incident() {
    }

    public Incident(String incidentId, String paymentId, String correlationId,
                    String incidentType, String severity, String status,
                    Integer riskScore, String failureReason, List<String> details) {
        this.incidentId = incidentId;
        this.paymentId = paymentId;
        this.correlationId = correlationId;
        this.incidentType = incidentType;
        this.severity = severity;
        this.status = status;
        this.riskScore = riskScore;
        this.failureReason = failureReason;
        this.details = details;
        this.createdAt = Instant.now();
    }

    @PrePersist
    @PreUpdate
    void syncTimestamps() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if ("ACKNOWLEDGED".equals(status) && acknowledgedAt == null) {
            acknowledgedAt = Instant.now();
        }
        if ("RESOLVED".equals(status) && resolvedAt == null) {
            resolvedAt = Instant.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        if ("ACKNOWLEDGED".equals(status) && acknowledgedAt == null) {
            acknowledgedAt = Instant.now();
        }
        if ("RESOLVED".equals(status) && resolvedAt == null) {
            resolvedAt = Instant.now();
        }
    }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(Instant acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    @Override
    public String toString() {
        return "Incident{" +
                "incidentId='" + incidentId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", incidentType='" + incidentType + '\'' +
                ", severity='" + severity + '\'' +
                ", status='" + status + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}

