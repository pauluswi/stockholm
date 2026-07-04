package com.europe.sepa.reporting.kafka;

import com.europe.sepa.reporting.entity.PaymentReport;
import com.europe.sepa.reporting.kafka.event.LedgerUpdatedEvent;
import com.europe.sepa.reporting.repository.PaymentReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Handles the business logic triggered by {@code ledger.updated} events.
 * Builds and persists a {@link PaymentReport} as the reporting read-model.
 */
@Service
public class ReportingService {

    private static final Logger log = LoggerFactory.getLogger(ReportingService.class);

    private final PaymentReportRepository reportRepository;

    public ReportingService(PaymentReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public PaymentReport handleLedgerUpdated(LedgerUpdatedEvent event) {
        log.info("[reporting] Received ledger.updated for payment={} status={} correlationId={}",
                event.getPaymentId(), event.getStatus(), event.getCorrelationId());

        // Upsert — idempotent if same ledgerEntryId arrives twice
        PaymentReport report = reportRepository.findById(event.getLedgerEntryId())
                .orElseGet(() -> new PaymentReport());

        report.setId(event.getLedgerEntryId());
        report.setPaymentId(event.getPaymentId());
        report.setSettlementId(event.getSettlementId());
        report.setStatus(event.getStatus());
        report.setCorrelationId(event.getCorrelationId());
        report.setSourceEventId(event.getEventId());
        report.setLedgerTimestamp(event.getTimestamp());
        report.setReportedAt(Instant.now());

        PaymentReport saved = reportRepository.save(report);
        log.info("[reporting] Saved PaymentReport id={} paymentId={}", saved.getId(), saved.getPaymentId());
        return saved;
    }
}

