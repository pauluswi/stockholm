package com.europe.sepa.backoffice.web.dto;

import java.time.Instant;

public record OverviewResponse(
        long totalReports,
        long settledReports,
        long failedReports,
        long flaggedAnomalies,
        long openIncidents,
        long criticalIncidents,
        Instant generatedAt
) {
}

