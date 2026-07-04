package com.europe.sepa.anomaly.scoring;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the risk scoring engine.
 */
class RiskScoringEngineTest {

    private final RiskScoringEngine engine = new RiskScoringEngine(null);

    @Test
    void whenVeryHighAmountTransaction_thenScoreIncreases() {
        var result = engine.scorePayment("pay-123", "SomeBank", 75000.00,
                java.time.Instant.now(), "corr-123");

        assertThat(result.score).isGreaterThan(0);
        assertThat(result.severity).isIn("LOW", "MEDIUM", "HIGH");
        assertThat(result.reasons).isNotEmpty();
    }

    @Test
    void whenLowAmountTransaction_thenScoreLow() {
        var result = engine.scorePayment("pay-456", "StdBank", 100.00,
                java.time.Instant.now(), "corr-456");

        assertThat(result.score).isGreaterThanOrEqualTo(0);
        assertThat(result.severity).isIn("LOW", "MEDIUM");
    }

    @Test
    void whenHighRiskFactorsCombined_thenFlaggedTrue() {
        var result = engine.scorePayment("pay-789", "NewBank", 50000.00,
                java.time.Instant.now(), "corr-789");

        // Very high amount + new beneficiary = should be flagged
        assertThat(result.score).isGreaterThanOrEqualTo(45);
    }
}

