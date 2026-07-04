package com.europe.sepa.resilience;

import com.europe.sepa.resilience.entity.Incident;
import com.europe.sepa.resilience.kafka.event.AnomalyDetectedEvent;
import com.europe.sepa.resilience.kafka.event.SettlementFailedEvent;
import com.europe.sepa.resilience.repository.IncidentRepository;
import com.europe.sepa.resilience.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for resilience monitor service.
 * Tests incident creation from anomaly and settlement failure events.
 */
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1, brokerProperties = {"log.dir=/tmp/kafka"})
@ActiveProfiles("test")
public class ResilienceMonitorIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentService incidentService;

    @BeforeEach
    public void setUp() {
        incidentRepository.deleteAll();
    }

    @Test
    public void whenAnomalyDetectedPublished_thenIncidentCreated() throws Exception {
        AnomalyDetectedEvent event = new AnomalyDetectedEvent(
            "EVT-ANOM-001",
            "anomaly.detected",
            "CORR-123",
            Instant.now(),
            "PAY-001",
            85,
            Arrays.asList(
                "Very high transaction amount (> €50,000)",
                "New beneficiary (first payment to this party)"
            ),
            "HIGH"
        );

        kafkaTemplate.send("anomaly.detected", event).get(5, TimeUnit.SECONDS);

        await().atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                var incidents = incidentRepository.findByPaymentId("PAY-001");
                assertFalse(incidents.isEmpty());
                
                Incident incident = incidents.get(0);
                assertEquals("ANOMALY_DETECTED", incident.getIncidentType());
                assertEquals("CRITICAL", incident.getSeverity());
                assertEquals("OPEN", incident.getStatus());
                assertEquals(85, incident.getRiskScore());
            });
    }

    @Test
    public void whenSettlementFailedPublished_thenFailureIncidentCreated() throws Exception {
        SettlementFailedEvent event = new SettlementFailedEvent(
            "EVT-FAIL-001",
            "settlement.failed",
            "CORR-456",
            Instant.now(),
            "PAY-002",
            "DE89370400440532013000",
            "IT60X0542811101000000123456",
            50000.00,
            "EUR",
            "Network timeout",
            2
        );

        kafkaTemplate.send("settlement.failed", event).get(5, TimeUnit.SECONDS);

        await().atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                var incidents = incidentRepository.findByPaymentId("PAY-002");
                assertFalse(incidents.isEmpty());
                
                Incident incident = incidents.get(0);
                assertEquals("SETTLEMENT_FAILED", incident.getIncidentType());
                assertEquals("HIGH", incident.getSeverity());
                assertEquals("OPEN", incident.getStatus());
                assertEquals("Network timeout", incident.getFailureReason());
            });
    }

    @Test
    public void whenMultipleFailuresPublished_thenDLQOverflowDetected() throws Exception {
        // Publish multiple settlement failures to trigger DLQ overflow
        for (int i = 0; i < 105; i++) {
            SettlementFailedEvent event = new SettlementFailedEvent(
                "EVT-FAIL-" + i,
                "settlement.failed",
                "CORR-" + i,
                Instant.now(),
                "PAY-" + i,
                "DE89370400440532013000",
                "IT60X0542811101000000123456",
                50000.00,
                "EUR",
                "Network timeout",
                3  // Retry count >= 3 triggers DLQ tracking
            );

            kafkaTemplate.send("settlement.failed", event).get(5, TimeUnit.SECONDS);
        }

        await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                var dlqIncidents = incidentRepository.findByIncidentType("DLQ_OVERFLOW");
                assertFalse(dlqIncidents.isEmpty());
                
                Incident dlqIncident = dlqIncidents.get(0);
                assertEquals("CRITICAL", dlqIncident.getSeverity());
                assertEquals("ESCALATED", dlqIncident.getStatus());
            });
    }

    @Test
    public void whenIncidentAcknowledged_thenStatusUpdated() {
        // Create an incident
        Incident incident = new Incident(
            "ANOM-12345678",
            "PAY-003",
            "CORR-789",
            "ANOMALY_DETECTED",
            "HIGH",
            "OPEN",
            80,
            null,
            Arrays.asList("Test reason")
        );

        Incident saved = incidentRepository.save(incident);

        Incident acknowledged = incidentService.acknowledgeIncident(saved.getId(), "john.doe");

        assertEquals("ACKNOWLEDGED", acknowledged.getStatus());
        assertEquals("john.doe", acknowledged.getAssignedTo());
        assertNotNull(acknowledged.getAcknowledgedAt());
    }

    @Test
    public void whenIncidentResolved_thenStatusUpdated() {
        // Create an incident
        Incident incident = new Incident(
            "ANOM-87654321",
            "PAY-004",
            "CORR-999",
            "ANOMALY_DETECTED",
            "MEDIUM",
            "OPEN",
            60,
            null,
            Arrays.asList("Test reason")
        );

        Incident saved = incidentRepository.save(incident);

        Incident resolved = incidentService.resolveIncident(saved.getId());

        assertEquals("RESOLVED", resolved.getStatus());
        assertNotNull(resolved.getResolvedAt());
    }
}

