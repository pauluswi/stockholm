package com.europe.sepa.resilience.web;

import com.europe.sepa.resilience.entity.Incident;
import com.europe.sepa.resilience.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API for operational resilience monitoring.
 * Exposes incident queries and operational metrics.
 */
@RestController
@RequestMapping("/resilience")
public class ResilienceController {

    private final IncidentService incidentService;

    public ResilienceController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    /**
     * Get all open incidents.
     * GET /resilience/incidents/open
     */
    @GetMapping("/incidents/open")
    public List<Incident> getOpenIncidents() {
        return incidentService.getOpenIncidents();
    }

    /**
     * Get all critical incidents.
     * GET /resilience/incidents/critical
     */
    @GetMapping("/incidents/critical")
    public List<Incident> getCriticalIncidents() {
        return incidentService.getCriticalIncidents();
    }

    /**
     * Get incident by ID.
     * GET /resilience/incidents/{id}
     */
    @GetMapping("/incidents/{id}")
    public ResponseEntity<Incident> getIncident(@PathVariable Long id) {
        try {
            // This is a simple lookup; in production, use findById() with proper handling
            var incidents = incidentService.getOpenIncidents();
            var incident = incidents.stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst();
            return incident.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get incidents for a payment.
     * GET /resilience/incidents/by-payment/{paymentId}
     */
    @GetMapping("/incidents/by-payment/{paymentId}")
    public List<Incident> getIncidentsByPayment(@PathVariable String paymentId) {
        return incidentService.getIncidentsByPayment(paymentId);
    }

    /**
     * Acknowledge an incident.
     * POST /resilience/incidents/{id}/acknowledge
     */
    @PostMapping("/incidents/{id}/acknowledge")
    public ResponseEntity<Incident> acknowledgeIncident(
            @PathVariable Long id,
            @RequestParam String assignedTo) {
        try {
            Incident incident = incidentService.acknowledgeIncident(id, assignedTo);
            return ResponseEntity.ok(incident);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Resolve an incident.
     * POST /resilience/incidents/{id}/resolve
     */
    @PostMapping("/incidents/{id}/resolve")
    public ResponseEntity<Incident> resolveIncident(@PathVariable Long id) {
        try {
            Incident incident = incidentService.resolveIncident(id);
            return ResponseEntity.ok(incident);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get operational metrics.
     * GET /resilience/metrics
     */
    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        List<Incident> openIncidents = incidentService.getOpenIncidents();
        List<Incident> criticalIncidents = incidentService.getCriticalIncidents();

        metrics.put("openIncidents", openIncidents.size());
        metrics.put("criticalIncidents", criticalIncidents.size());
        metrics.put("dlqMessageCount", incidentService.getDLQMessageCount());
        metrics.put("dlqThreshold", 100);
        metrics.put("systemHealth", calculateSystemHealth(openIncidents, criticalIncidents));

        return metrics;
    }

    /**
     * Get DLQ status.
     * GET /resilience/dlq/status
     */
    @GetMapping("/dlq/status")
    public Map<String, Object> getDLQStatus() {
        Map<String, Object> status = new HashMap<>();
        int messageCount = incidentService.getDLQMessageCount();
        int threshold = 100;

        status.put("messageCount", messageCount);
        status.put("threshold", threshold);
        status.put("percentageOfThreshold", (messageCount * 100) / threshold);
        status.put("status", messageCount >= threshold ? "OVERFLOW" : "OK");

        return status;
    }

    /**
     * Reset DLQ counter.
     * POST /resilience/dlq/reset
     */
    @PostMapping("/dlq/reset")
    public Map<String, String> resetDLQ() {
        incidentService.resetDLQCounter();

        Map<String, String> response = new HashMap<>();
        response.put("message", "DLQ counter reset");
        response.put("status", "OK");

        return response;
    }

    private String calculateSystemHealth(List<Incident> openIncidents, List<Incident> criticalIncidents) {
        if (criticalIncidents.size() > 0) {
            return "CRITICAL";
        }
        if (openIncidents.size() > 5) {
            return "DEGRADED";
        }
        if (openIncidents.size() > 0) {
            return "WARNING";
        }
        return "HEALTHY";
    }
}

