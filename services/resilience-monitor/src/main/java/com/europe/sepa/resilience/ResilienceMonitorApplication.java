package com.europe.sepa.resilience;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Resilience Monitor Application.
 * 
 * Operational resilience hub that monitors payment system health by:
 * - Consuming anomaly.detected events
 * - Consuming settlement.failed events
 * - Creating and tracking incidents
 * - Monitoring dead-letter queue (DLQ) overflow
 */
@SpringBootApplication
public class ResilienceMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResilienceMonitorApplication.class, args);
    }
}

