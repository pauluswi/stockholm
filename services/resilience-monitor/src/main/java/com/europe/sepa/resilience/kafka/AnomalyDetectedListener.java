package com.europe.sepa.resilience.kafka;

import com.europe.sepa.resilience.kafka.event.AnomalyDetectedEvent;
import com.europe.sepa.resilience.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for anomaly.detected events.
 * Creates incidents when anomalies are detected.
 */
@Component
public class AnomalyDetectedListener {

    private static final Logger log = LoggerFactory.getLogger(AnomalyDetectedListener.class);

    private final IncidentService incidentService;

    public AnomalyDetectedListener(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    /**
     * Listen for anomaly.detected events and create incidents.
     */
    @KafkaListener(topics = "anomaly.detected", groupId = "resilience-monitor-group")
    public void onAnomalyDetected(AnomalyDetectedEvent event) {
        log.info("[anomaly-listener] Received anomaly event: {}", event);

        try {
            incidentService.createAnomalyIncident(
                event.getPaymentId(),
                event.getCorrelationId(),
                event.getRiskScore(),
                event.getReasons(),
                event.getSeverity()
            );
        } catch (Exception e) {
            log.error("[anomaly-listener] Error processing anomaly event: {}", event, e);
        }
    }
}

