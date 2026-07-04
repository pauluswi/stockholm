package com.europe.sepa.anomaly.scoring;

import com.europe.sepa.anomaly.repository.AnomalyScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Explainable rule-based risk scoring engine.
 * Evaluates transactions on multiple dimensions without ML dependencies.
 */
@Component
public class RiskScoringEngine {

    private static final Logger log = LoggerFactory.getLogger(RiskScoringEngine.class);

    private static final double HIGH_AMOUNT_THRESHOLD = 10000.0;
    private static final double VERY_HIGH_AMOUNT_THRESHOLD = 50000.0;
    private static final int RISK_THRESHOLD = 75;

    private final AnomalyScoreRepository repository;

    public RiskScoringEngine(AnomalyScoreRepository repository) {
        this.repository = repository;
    }

    /**
     * Calculate risk score for a payment transaction.
     * Returns reasons for transparency.
     */
    public RiskScoreResult scorePayment(String paymentId, String beneficiary, double amount,
                                        Instant timestamp, String correlationId) {
        List<String> reasons = new ArrayList<>();
        int score = 0;

        // 1. High amount risk (0-30 points)
        if (amount >= VERY_HIGH_AMOUNT_THRESHOLD) {
            score += 30;
            reasons.add("Very high transaction amount (> €50,000)");
        } else if (amount >= HIGH_AMOUNT_THRESHOLD) {
            score += 20;
            reasons.add("High transaction amount (> €10,000)");
        } else if (amount >= 5000) {
            score += 10;
            reasons.add("Moderate transaction amount");
        }

        // 2. New beneficiary risk (0-25 points)
        if (isNewBeneficiary(beneficiary)) {
            score += 25;
            reasons.add("New beneficiary (first payment to this party)");
        }

        // 3. Rapid payment frequency (0-20 points)
        int recentPaymentCount = countRecentPayments(paymentId);
        if (recentPaymentCount >= 5) {
            score += 20;
            reasons.add("Rapid payment frequency (5+ payments in 24h)");
        } else if (recentPaymentCount >= 3) {
            score += 10;
            reasons.add("Elevated payment frequency (3+ payments in 24h)");
        }

        // 4. Unusual time of day (0-15 points)
        if (isUnusualTime(timestamp)) {
            score += 15;
            reasons.add("Payment outside business hours");
        }

        // 5. Round amount (0-10 points) - often indicates test/fraud
        if (isRoundAmount(amount)) {
            score += 10;
            reasons.add("Round amount (potential test/fraud indicator)");
        }

        String severity = determineSeverity(score);
        log.info("[anomaly] Payment {} scored {}/100 severity={} reasons={}", 
                 paymentId, score, severity, reasons);

        return new RiskScoreResult(score, severity, reasons);
    }

    private boolean isNewBeneficiary(String beneficiary) {
        // In a real system, query transaction history from ledger
        // For now, simple heuristic: exact match in DB
        return !beneficiary.toLowerCase().contains("standard") &&
               !beneficiary.toLowerCase().contains("trusted");
    }

    private int countRecentPayments(String paymentId) {
        // In a real system, query ledger/payment records for last 24h
        // For now, return 0 (no historical data in single transaction)
        return 0;
    }

    private boolean isUnusualTime(Instant timestamp) {
        ZonedDateTime zdt = timestamp.atZone(ZoneId.of("Europe/Berlin"));
        int hour = zdt.getHour();
        int dayOfWeek = zdt.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

        // Unusual if: midnight-6am, or weekend
        return (hour < 6 || hour > 20) || (dayOfWeek >= 6);
    }

    private boolean isRoundAmount(double amount) {
        // Check if amount is suspiciously round (1000, 5000, 10000, etc.)
        return amount % 1000 == 0;
    }

    private String determineSeverity(int score) {
        if (score >= 75) return "HIGH";
        if (score >= 50) return "MEDIUM";
        return "LOW";
    }

    /**
     * Result object combining score, severity, and explanation.
     */
    public static class RiskScoreResult {
        public final int score;
        public final String severity;
        public final List<String> reasons;

        public RiskScoreResult(int score, String severity, List<String> reasons) {
            this.score = score;
            this.severity = severity;
            this.reasons = reasons;
        }

        public boolean isFlagged() {
            return score >= RISK_THRESHOLD;
        }
    }
}

