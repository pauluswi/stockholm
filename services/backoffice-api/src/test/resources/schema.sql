CREATE TABLE payment_reports (
    id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL,
    settlement_id VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    correlation_id VARCHAR(64),
    source_event_id VARCHAR(64),
    ledger_timestamp TIMESTAMP,
    reported_at TIMESTAMP NOT NULL
);

CREATE TABLE ledger_entries (
    id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL,
    settlement_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    correlation_id VARCHAR(64) NOT NULL,
    source_event_id VARCHAR(64) NOT NULL,
    source_event_type VARCHAR(64) NOT NULL,
    source_timestamp TIMESTAMP NOT NULL,
    processed_at TIMESTAMP NOT NULL
);

CREATE TABLE anomaly_scores (
    id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL,
    risk_score INT NOT NULL,
    severity VARCHAR(32) NOT NULL,
    risk_factors VARCHAR(2000),
    correlation_id VARCHAR(64),
    event_timestamp TIMESTAMP,
    scored_at TIMESTAMP NOT NULL,
    flagged BOOLEAN DEFAULT FALSE
);

CREATE TABLE incidents (
    id BIGINT PRIMARY KEY,
    incident_id VARCHAR(64) NOT NULL,
    payment_id VARCHAR(64) NOT NULL,
    correlation_id VARCHAR(64) NOT NULL,
    incident_type VARCHAR(64) NOT NULL,
    severity VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    risk_score INT,
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    acknowledged_at TIMESTAMP,
    resolved_at TIMESTAMP,
    assigned_to VARCHAR(64)
);

