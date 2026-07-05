package com.europe.sepa.backoffice.web.dto;

import com.europe.sepa.backoffice.entity.AnomalyScoreView;
import com.europe.sepa.backoffice.entity.IncidentView;
import com.europe.sepa.backoffice.entity.LedgerEntryView;
import com.europe.sepa.backoffice.entity.PaymentReportView;

import java.util.List;

public record CorrelationSearchResponse(
        String correlationId,
        List<LedgerEntryView> ledgerEntries,
        List<PaymentReportView> reports,
        List<AnomalyScoreView> anomalyScores,
        List<IncidentView> incidents
) {
}

