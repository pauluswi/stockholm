package com.europe.sepa.reporting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Read-model entity built from {@code ledger.updated} events.
 * Represents the final state of a SEPA payment journey for reporting purposes.
 */
@Entity
@Table(name = "payment_reports")
public class PaymentReport {

    @Id
    private String id;                    // ledgerEntryId from the event

    @Column(nullable = false)
    private String paymentId;

    @Column
    private String settlementId;

    @Column(nullable = false)
    private String status;

    @Column
    private String correlationId;

    @Column
    private String sourceEventId;

    @Column
    private Instant ledgerTimestamp;

    @Column(nullable = false)
    private Instant reportedAt;

    public PaymentReport() {
    }

    public PaymentReport(String id, String paymentId, String settlementId, String status,
                         String correlationId, String sourceEventId,
                         Instant ledgerTimestamp, Instant reportedAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.settlementId = settlementId;
        this.status = status;
        this.correlationId = correlationId;
        this.sourceEventId = sourceEventId;
        this.ledgerTimestamp = ledgerTimestamp;
        this.reportedAt = reportedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getSettlementId() { return settlementId; }
    public void setSettlementId(String settlementId) { this.settlementId = settlementId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getSourceEventId() { return sourceEventId; }
    public void setSourceEventId(String sourceEventId) { this.sourceEventId = sourceEventId; }

    public Instant getLedgerTimestamp() { return ledgerTimestamp; }
    public void setLedgerTimestamp(Instant ledgerTimestamp) { this.ledgerTimestamp = ledgerTimestamp; }

    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
}

