package com.europe.sepa.reporting.web;

import com.europe.sepa.reporting.entity.PaymentReport;
import com.europe.sepa.reporting.repository.PaymentReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for querying payment reports.
 * Reports are built from {@code ledger.updated} Kafka events.
 */
@RestController
@RequestMapping("/reports")
public class ReportingController {

    private final PaymentReportRepository reportRepository;

    public ReportingController(PaymentReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * List all reports, optionally filtered by status.
     * GET /reports
     * GET /reports?status=SETTLED
     */
    @GetMapping
    public List<PaymentReport> listReports(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return reportRepository.findByStatus(status);
        }
        return reportRepository.findAll();
    }

    /**
     * Get a specific report by ledger entry id.
     * GET /reports/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentReport> getReport(@PathVariable String id) {
        return reportRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all reports for a given payment.
     * GET /reports/by-payment/{paymentId}
     */
    @GetMapping("/by-payment/{paymentId}")
    public List<PaymentReport> getByPayment(@PathVariable String paymentId) {
        return reportRepository.findByPaymentId(paymentId);
    }
}

