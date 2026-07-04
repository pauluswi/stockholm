package com.europe.sepa.anomaly.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for liveness probe.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("UP");
    }

    static class HealthResponse {
        public String status;

        HealthResponse(String status) {
            this.status = status;
        }
    }
}

