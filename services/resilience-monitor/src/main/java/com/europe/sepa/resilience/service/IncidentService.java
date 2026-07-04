package com.europe.sepa.resilience.service;

import com.europe.sepa.resilience.entity.Incident;
import com.europe.sepa.resilience.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for creating and managing incidents.
 * Implements operational resilience patterns including:
 * - Incident creation from anomalies and failures
 * - DLQ overflow tracking
 * - Incident lifecycle management
 */
@Service
public class IncidentService {

    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);

    private final IncidentRepository incidentRepository;
    private final AtomicInteger dlqOverflowCounter = new AtomicInteger(0);
    private static final int DLQ_OVERFLOW_THRESHOLD = 100;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    /**
     * Create incident from anomaly detection event.
     */
    public Incident createAnomalyIncident(String paymentId, String correlationId,
                                         int riskScore, List<String> reasons, String severity) {
        String incidentId = generateIncidentId("ANOM");
        String incidentSeverity = mapAnomalySeverityToIncidentSeverity(severity, riskScore);

        Incident incident = new Incident(
            incidentId,
            paymentId,
            correlationId,
            "ANOMALY_DETECTED",
            incidentSeverity,
            "OPEN",
            riskScore,
            null,
            reasons
        );

        Incident saved = incidentRepository.save(incident);
        log.info("[resilience] Created ANOMALY incident {} for payment {} (risk={}, severity={})",
                 incidentId, paymentId, riskScore, incidentSeverity);

        return saved;
    }

    /**
     * Create incident from settlement failure event.
     */
    public Incident createSettlementFailureIncident(String paymentId, String correlationId,
                                                    String failureReason, int retryCount) {
        String incidentId = generateIncidentId("FAIL");
        String severity = determineFailureSeverity(retryCount);

        List<String> details = Arrays.asList(
            "Payment failed to settle",
            "Failure reason: " + failureReason,
            "Retry count: " + retryCount,
            "Status: Requires manual intervention"
        );

        Incident incident = new Incident(
            incidentId,
            paymentId,
            correlationId,
            "SETTLEMENT_FAILED",
            severity,
            "OPEN",
            null,
            failureReason,
            details
        );

        Incident saved = incidentRepository.save(incident);
        log.info("[resilience] Created SETTLEMENT_FAILED incident {} for payment {} (retries={})",
                 incidentId, paymentId, retryCount);

        return saved;
    }

    /**
     * Track DLQ (Dead Letter Queue) messages.
     * If overflow threshold exceeded, create CRITICAL incident.
     */
    public Optional<Incident> trackDLQMessage(String paymentId, String correlationId,
                                              String topic, String failureReason) {
        int currentCount = dlqOverflowCounter.incrementAndGet();
        log.warn("[resilience] DLQ message received (count={}): payment={}, topic={}",
                 currentCount, paymentId, topic);

        if (currentCount >= DLQ_OVERFLOW_THRESHOLD) {
            String incidentId = generateIncidentId("DLQ");

            List<String> details = Arrays.asList(
                "DLQ overflow detected",
                "Threshold exceeded: " + DLQ_OVERFLOW_THRESHOLD + " messages",
                "Topic: " + topic,
                "Failure reason: " + failureReason,
                "Requires escalation and manual inspection"
            );

            Incident incident = new Incident(
                incidentId,
                paymentId,
                correlationId,
                "DLQ_OVERFLOW",
                "CRITICAL",
                "ESCALATED",
                null,
                failureReason,
                details
            );

            Incident saved = incidentRepository.save(incident);
            log.error("[resilience] CRITICAL: DLQ overflow incident {} created (count={})",
                      incidentId, currentCount);

            // Reset counter after creating incident
            dlqOverflowCounter.set(0);

            return Optional.of(saved);
        }

        return Optional.empty();
    }

    /**
     * Acknowledge an incident.
     */
    public Incident acknowledgeIncident(Long incidentId, String assignedTo) {
        Optional<Incident> opt = incidentRepository.findById(incidentId);

        if (opt.isPresent()) {
            Incident incident = opt.get();
            incident.setStatus("ACKNOWLEDGED");
            incident.setAcknowledgedAt(Instant.now());
            incident.setAssignedTo(assignedTo);

            Incident saved = incidentRepository.save(incident);
            log.info("[resilience] Incident {} acknowledged by {}", incident.getIncidentId(), assignedTo);

            return saved;
        }

        throw new IllegalArgumentException("Incident not found: " + incidentId);
    }

    /**
     * Resolve an incident.
     */
    public Incident resolveIncident(Long incidentId) {
        Optional<Incident> opt = incidentRepository.findById(incidentId);

        if (opt.isPresent()) {
            Incident incident = opt.get();
            incident.setStatus("RESOLVED");
            incident.setResolvedAt(Instant.now());

            Incident saved = incidentRepository.save(incident);
            log.info("[resilience] Incident {} resolved", incident.getIncidentId());

            return saved;
        }

        throw new IllegalArgumentException("Incident not found: " + incidentId);
    }

    /**
     * Get open incidents.
     */
    public List<Incident> getOpenIncidents() {
        return incidentRepository.findByStatusOrderByCreatedAtDesc("OPEN");
    }

    /**
     * Get incidents for a payment.
     */
    public List<Incident> getIncidentsByPayment(String paymentId) {
        return incidentRepository.findByPaymentId(paymentId);
    }

    /**
     * Get critical incidents.
     */
    public List<Incident> getCriticalIncidents() {
        return incidentRepository.findBySeverity("CRITICAL");
    }

    /**
     * Get DLQ counter.
     */
    public int getDLQMessageCount() {
        return dlqOverflowCounter.get();
    }

    /**
     * Reset DLQ counter (e.g., after manual remediation).
     */
    public void resetDLQCounter() {
        dlqOverflowCounter.set(0);
        log.info("[resilience] DLQ counter reset");
    }

    private String generateIncidentId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String mapAnomalySeverityToIncidentSeverity(String anomalySeverity, int riskScore) {
        if ("HIGH".equals(anomalySeverity)) {
            return riskScore >= 85 ? "CRITICAL" : "HIGH";
        }
        return "MEDIUM";
    }

    private String determineFailureSeverity(int retryCount) {
        if (retryCount >= 3) {
            return "CRITICAL";
        } else if (retryCount >= 2) {
            return "HIGH";
        }
        return "MEDIUM";
    }
}
