package com.europe.sepa.backoffice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "payment_reports")
public class PaymentReportView {

    @Id
    private String id;

    @Column(nullable = false)
    private String paymentId;

    private String settlementId;

    @Column(nullable = false)
    private String status;

    private String correlationId;

    private String sourceEventId;

    private Instant ledgerTimestamp;

    @Column(nullable = false)
    private Instant reportedAt;

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

    public Instant getLedgerTimestamp() {
        return ledgerTimestamp;
    }

    public Instant getReportedAt() {
        return reportedAt;
    }
}

