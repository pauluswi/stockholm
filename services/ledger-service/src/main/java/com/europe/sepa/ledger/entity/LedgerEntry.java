package com.europe.sepa.ledger.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Ledger record persisted after settlement.
 */
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String paymentId;

    @Column(nullable = false)
    private String settlementId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String sourceEventId;

    @Column(nullable = false)
    private String sourceEventType;

    @Column(nullable = false)
    private Instant sourceTimestamp;

    @Column(nullable = false)
    private Instant processedAt;

    public LedgerEntry() {
    }

    public LedgerEntry(String id, String paymentId, String settlementId, String status, String correlationId,
                       String sourceEventId, String sourceEventType, Instant sourceTimestamp, Instant processedAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.settlementId = settlementId;
        this.status = status;
        this.correlationId = correlationId;
        this.sourceEventId = sourceEventId;
        this.sourceEventType = sourceEventType;
        this.sourceTimestamp = sourceTimestamp;
        this.processedAt = processedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getSourceEventId() {
        return sourceEventId;
    }

    public void setSourceEventId(String sourceEventId) {
        this.sourceEventId = sourceEventId;
    }

    public String getSourceEventType() {
        return sourceEventType;
    }

    public void setSourceEventType(String sourceEventType) {
        this.sourceEventType = sourceEventType;
    }

    public Instant getSourceTimestamp() {
        return sourceTimestamp;
    }

    public void setSourceTimestamp(Instant sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}

