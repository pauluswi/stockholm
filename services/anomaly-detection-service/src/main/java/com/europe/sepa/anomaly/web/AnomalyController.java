package com.europe.sepa.anomaly.web;

import com.europe.sepa.anomaly.entity.AnomalyScore;
import com.europe.sepa.anomaly.repository.AnomalyScoreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for querying anomaly detection results.
 */
@RestController
@RequestMapping("/anomalies")
public class AnomalyController {

    private final AnomalyScoreRepository scoreRepository;

    public AnomalyController(AnomalyScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    /**
     * Get anomaly score for a payment.
     * GET /anomalies/by-payment/{paymentId}
     */
    @GetMapping("/by-payment/{paymentId}")
    public ResponseEntity<AnomalyScore> getPaymentAnomaly(@PathVariable String paymentId) {
        return scoreRepository.findByPaymentId(paymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * List all flagged anomalies (high risk).
     * GET /anomalies/flagged
     */
    @GetMapping("/flagged")
    public List<AnomalyScore> getFlaggedAnomalies() {
        return scoreRepository.findByFlaggedTrue();
    }

    /**
     * List anomalies by severity.
     * GET /anomalies/severity/{severity}
     */
    @GetMapping("/severity/{severity}")
    public List<AnomalyScore> getBySeverity(@PathVariable String severity) {
        return scoreRepository.findBySeverity(severity);
    }
}


