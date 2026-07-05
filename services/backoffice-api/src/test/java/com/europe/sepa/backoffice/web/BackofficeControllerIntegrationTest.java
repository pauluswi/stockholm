package com.europe.sepa.backoffice.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BackofficeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void overviewReturnsAggregatedMetrics() throws Exception {
        mockMvc.perform(get("/backoffice/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReports").value(2))
                .andExpect(jsonPath("$.settledReports").value(1))
                .andExpect(jsonPath("$.failedReports").value(1))
                .andExpect(jsonPath("$.flaggedAnomalies").value(1))
                .andExpect(jsonPath("$.openIncidents").value(1))
                .andExpect(jsonPath("$.criticalIncidents").value(1));
    }

    @Test
    void paymentTimelineReturnsCrossServiceData() throws Exception {
        mockMvc.perform(get("/backoffice/payments/PAY-001/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY-001"))
                .andExpect(jsonPath("$.ledgerEntry.id").value("LE-1"))
                .andExpect(jsonPath("$.reports[0].status").value("SETTLED"))
                .andExpect(jsonPath("$.anomalyScores[0].riskScore").value(88))
                .andExpect(jsonPath("$.incidents[0].incidentId").value("INC-001"));
    }

    @Test
    void correlationSearchReturnsLinkedRows() throws Exception {
        mockMvc.perform(get("/backoffice/correlations/CORR-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correlationId").value("CORR-001"))
                .andExpect(jsonPath("$.ledgerEntries.length()").value(1))
                .andExpect(jsonPath("$.reports.length()").value(1))
                .andExpect(jsonPath("$.anomalyScores.length()").value(1))
                .andExpect(jsonPath("$.incidents.length()").value(1));
    }

    @Test
    void incidentFilterWorksByStatusAndSeverity() throws Exception {
        mockMvc.perform(get("/backoffice/incidents")
                        .param("status", "OPEN")
                        .param("severity", "CRITICAL")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].incidentId").value("INC-001"));
    }

    @Test
    void flaggedAnomalyFilterWorks() throws Exception {
        mockMvc.perform(get("/backoffice/anomalies/flagged")
                        .param("severity", "HIGH")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("AN-1"));
    }
}

