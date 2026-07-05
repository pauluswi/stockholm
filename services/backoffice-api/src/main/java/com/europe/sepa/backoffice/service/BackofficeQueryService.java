package com.europe.sepa.backoffice.service;

import com.europe.sepa.backoffice.entity.AnomalyScoreView;
import com.europe.sepa.backoffice.entity.IncidentView;
import com.europe.sepa.backoffice.entity.LedgerEntryView;
import com.europe.sepa.backoffice.entity.PaymentReportView;
import com.europe.sepa.backoffice.repository.AnomalyScoreViewRepository;
import com.europe.sepa.backoffice.repository.IncidentViewRepository;
import com.europe.sepa.backoffice.repository.LedgerEntryViewRepository;
import com.europe.sepa.backoffice.repository.PaymentReportViewRepository;
import com.europe.sepa.backoffice.web.dto.CorrelationSearchResponse;
import com.europe.sepa.backoffice.web.dto.OverviewResponse;
import com.europe.sepa.backoffice.web.dto.PaymentTimelineResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BackofficeQueryService {

    private final PaymentReportViewRepository reportRepository;
    private final LedgerEntryViewRepository ledgerRepository;
    private final AnomalyScoreViewRepository anomalyRepository;
    private final IncidentViewRepository incidentRepository;

    public BackofficeQueryService(PaymentReportViewRepository reportRepository,
                                  LedgerEntryViewRepository ledgerRepository,
                                  AnomalyScoreViewRepository anomalyRepository,
                                  IncidentViewRepository incidentRepository) {
        this.reportRepository = reportRepository;
        this.ledgerRepository = ledgerRepository;
        this.anomalyRepository = anomalyRepository;
        this.incidentRepository = incidentRepository;
    }

    public OverviewResponse getOverview() {
        return new OverviewResponse(
                reportRepository.count(),
                reportRepository.countByStatus("SETTLED"),
                reportRepository.countByStatus("FAILED"),
                anomalyRepository.countByFlaggedTrue(),
                incidentRepository.countByStatus("OPEN"),
                incidentRepository.countBySeverity("CRITICAL"),
                Instant.now()
        );
    }

    public PaymentTimelineResponse getPaymentTimeline(String paymentId) {
        LedgerEntryView ledgerEntry = ledgerRepository.findByPaymentId(paymentId).orElse(null);
        List<PaymentReportView> reports = reportRepository.findByPaymentId(paymentId);
        List<AnomalyScoreView> anomalies = anomalyRepository.findByPaymentId(paymentId);
        List<IncidentView> incidents = incidentRepository.findByPaymentId(paymentId);

        return new PaymentTimelineResponse(paymentId, ledgerEntry, reports, anomalies, incidents);
    }

    public CorrelationSearchResponse getByCorrelationId(String correlationId) {
        return new CorrelationSearchResponse(
                correlationId,
                ledgerRepository.findByCorrelationId(correlationId),
                reportRepository.findByCorrelationId(correlationId),
                anomalyRepository.findByCorrelationId(correlationId),
                incidentRepository.findByCorrelationId(correlationId)
        );
    }

    public List<IncidentView> searchIncidents(String status, String severity, int limit) {
        int size = Math.max(1, Math.min(limit, 200));
        return incidentRepository.search(status, severity, PageRequest.of(0, size)).getContent();
    }

    public List<AnomalyScoreView> flaggedAnomalies(String severity, int limit) {
        int size = Math.max(1, Math.min(limit, 200));
        return anomalyRepository.findFlagged(severity, PageRequest.of(0, size)).getContent();
    }
}

