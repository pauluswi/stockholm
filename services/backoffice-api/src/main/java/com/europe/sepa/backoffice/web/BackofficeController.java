package com.europe.sepa.backoffice.web;

import com.europe.sepa.backoffice.entity.AnomalyScoreView;
import com.europe.sepa.backoffice.entity.IncidentView;
import com.europe.sepa.backoffice.service.BackofficeQueryService;
import com.europe.sepa.backoffice.web.dto.CorrelationSearchResponse;
import com.europe.sepa.backoffice.web.dto.OverviewResponse;
import com.europe.sepa.backoffice.web.dto.PaymentTimelineResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backoffice")
public class BackofficeController {

    private final BackofficeQueryService queryService;

    public BackofficeController(BackofficeQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/overview")
    public OverviewResponse overview() {
        return queryService.getOverview();
    }

    @GetMapping("/payments/{paymentId}/timeline")
    public PaymentTimelineResponse paymentTimeline(@PathVariable String paymentId) {
        return queryService.getPaymentTimeline(paymentId);
    }

    @GetMapping("/correlations/{correlationId}")
    public CorrelationSearchResponse searchByCorrelation(@PathVariable String correlationId) {
        return queryService.getByCorrelationId(correlationId);
    }

    @GetMapping("/incidents")
    public List<IncidentView> incidents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return queryService.searchIncidents(status, severity, limit);
    }

    @GetMapping("/anomalies/flagged")
    public List<AnomalyScoreView> anomalies(
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "50") int limit
    ) {
        return queryService.flaggedAnomalies(severity, limit);
    }
}

