package com.europe.sepa.resilience.kafka;

import com.europe.sepa.resilience.kafka.event.SettlementFailedEvent;
import com.europe.sepa.resilience.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for settlement.failed events.
 * Creates incidents when settlement failures occur and tracks DLQ overflow.
 */
@Component
public class SettlementFailedListener {

    private static final Logger log = LoggerFactory.getLogger(SettlementFailedListener.class);

    private final IncidentService incidentService;

    public SettlementFailedListener(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    /**
     * Listen for settlement.failed events and create incidents.
     */
    @KafkaListener(topics = "settlement.failed", groupId = "resilience-monitor-group")
    public void onSettlementFailed(SettlementFailedEvent event) {
        log.info("[settlement-listener] Received settlement failure event: {}", event);

        try {
            // Create incident for settlement failure
            incidentService.createSettlementFailureIncident(
                event.getPaymentId(),
                event.getCorrelationId(),
                event.getFailureReason(),
                event.getRetryCount()
            );

            // Check if DLQ overflow should be tracked
            if (event.getRetryCount() >= 3) {
                var dlqIncident = incidentService.trackDLQMessage(
                    event.getPaymentId(),
                    event.getCorrelationId(),
                    "settlement.failed",
                    event.getFailureReason()
                );

                if (dlqIncident.isPresent()) {
                    log.error("[settlement-listener] DLQ overflow detected: {}", dlqIncident.get());
                }
            }
        } catch (Exception e) {
            log.error("[settlement-listener] Error processing settlement failure event: {}", event, e);
        }
    }
}

