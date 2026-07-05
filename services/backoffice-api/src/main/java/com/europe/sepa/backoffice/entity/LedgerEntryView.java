package com.europe.sepa.backoffice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryView {

    @Id
    private String id;

    @Column(nullable = false)
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

    public String getId() {
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public String getStatus() {
        return status;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getSourceEventId() {
        return sourceEventId;
    }

    public String getSourceEventType() {
        return sourceEventType;
    }

    public Instant getSourceTimestamp() {
        return sourceTimestamp;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}

