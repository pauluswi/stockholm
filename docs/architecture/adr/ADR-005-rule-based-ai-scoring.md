# ADR-005: Rule-Based AI Scoring for Anomaly Detection

## Status
Accepted

## Context

Stockholm must demonstrate transaction monitoring and anomaly detection, a key DORA operational resilience component. The system needs to:

- Detect suspicious payment patterns
- Explain risk factors clearly (explainability requirement)
- Identify high-risk transactions for manual review
- Support future ML model integration
- Work reliably with simulated data

Two approaches exist:

1. **Machine Learning**: Train models on real payment data
2. **Rule-Based Scoring**: Define heuristic rules for risk signals

For an educational showcase:
- Real payment data unavailable
- Complex ML pipelines beyond scope
- Explainability essential for understanding
- But architecture must support future ML

## Decision

We have chosen a **rule-based scoring engine** for initial implementation, with architecture supporting future ML integration.

Scoring factors analyzed:
- Transaction amount (unusual highs)
- Payment velocity (frequency analysis)
- Beneficiary status (new vs. established)
- Time of day (unusual hours)
- Country/region risk profiles
- Repeated failures patterns

Scoring mechanism:
- Each factor contributes 0-20 points
- Total score 0-100
- Threshold (e.g., 75+) triggers anomaly event
- Each risk factor is documented with reason

## Consequences

### Positive
- **Explainability**: Clear rules easy to understand and validate
- **Deterministic**: Same input always produces same score
- **Easy to adjust**: Rules can be tuned by business users
- **No data required**: Works with simulated transactions
- **Debugging**: Clear audit trail of scoring decisions
- **Regulatory friendly**: Rule basis easier for compliance
- **Reliable**: No model drift or unexpected behavior
- **Composable**: Easy to add new signals

### Negative
- **Limited sophistication**: Can't detect complex patterns
- **Manual tuning**: Requires domain expertise to set thresholds
- **Not adaptive**: Doesn't learn from new patterns
- **False positives**: Rule-based systems often over-flag

### Trade-offs
- Chose explainability and reliability over sophistication
- Accepted manual tuning for educational clarity

## Alternatives Considered

### Machine Learning Model
- **Pros**: Can detect complex patterns, adaptive
- **Cons**: Requires training data, opaque decisions, drift issues

### Hybrid Approach
- **Pros**: Best of both worlds
- **Cons**: More complex, harder to explain

### Threshold-Based (Simple)
- **Pros**: Minimal complexity
- **Cons**: Can't combine multiple signals intelligently

## Decision Drivers

1. **Educational value**: Clear rules easier to understand for learning
2. **Explainability**: Regulatory focus on AI transparency
3. **No training data**: Showcase can't use real payment data
4. **Flexibility**: Architecture supports future ML replacement

## Architecture Pattern

The scoring engine follows a strategy pattern:

```
RiskScoringService
├── AmountSignal (unusually high amount)
├── VelocitySignal (payment frequency)
├── BeneficiarySignal (new recipient)
├── TimeSignal (unusual hour)
├── CountryRiskSignal (destination risk)
└── RetrySignal (repeated failures)
```

Each signal:
- Evaluates transaction context
- Returns score contribution (0-20)
- Provides explanation string
- Can be enabled/disabled independently

## Future Evolution

ML model integration approach:
1. Create MLScoringService implementing same interface
2. Models deployed as separate service
3. Strategy pattern enables switching at runtime
4. Logging/monitoring tracks differences between approaches
5. Gradual migration as confidence builds

## Related Decisions

- Complements ADR-002 (Event-Driven Architecture) - Anomaly events trigger downstream
- Documented in arc42 section 8 (Cross-Cutting Concepts)
- Enables DORA operational resilience (incident creation)

