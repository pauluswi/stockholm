INSERT INTO payment_reports (id, payment_id, settlement_id, status, correlation_id, source_event_id, ledger_timestamp, reported_at)
VALUES
('LE-1', 'PAY-001', 'SET-001', 'SETTLED', 'CORR-001', 'EVT-1', TIMESTAMP '2026-07-05 10:00:00', TIMESTAMP '2026-07-05 10:00:05'),
('LE-2', 'PAY-002', 'SET-002', 'FAILED', 'CORR-002', 'EVT-2', TIMESTAMP '2026-07-05 11:00:00', TIMESTAMP '2026-07-05 11:00:05');

INSERT INTO ledger_entries (id, payment_id, settlement_id, status, correlation_id, source_event_id, source_event_type, source_timestamp, processed_at)
VALUES
('LE-1', 'PAY-001', 'SET-001', 'SETTLED', 'CORR-001', 'EVT-1', 'SettlementCompletedEvent', TIMESTAMP '2026-07-05 10:00:00', TIMESTAMP '2026-07-05 10:00:02');

INSERT INTO anomaly_scores (id, payment_id, risk_score, severity, risk_factors, correlation_id, event_timestamp, scored_at, flagged)
VALUES
('AN-1', 'PAY-001', 88, 'HIGH', 'High amount|New beneficiary', 'CORR-001', TIMESTAMP '2026-07-05 10:00:01', TIMESTAMP '2026-07-05 10:00:03', TRUE),
('AN-2', 'PAY-002', 45, 'LOW', 'None', 'CORR-002', TIMESTAMP '2026-07-05 11:00:01', TIMESTAMP '2026-07-05 11:00:03', FALSE);

INSERT INTO incidents (id, incident_id, payment_id, correlation_id, incident_type, severity, status, risk_score, failure_reason, created_at, acknowledged_at, resolved_at, assigned_to)
VALUES
(1, 'INC-001', 'PAY-001', 'CORR-001', 'ANOMALY_DETECTED', 'CRITICAL', 'OPEN', 88, NULL, TIMESTAMP '2026-07-05 10:01:00', NULL, NULL, NULL),
(2, 'INC-002', 'PAY-002', 'CORR-002', 'SETTLEMENT_FAILED', 'HIGH', 'RESOLVED', NULL, 'Network timeout', TIMESTAMP '2026-07-05 11:01:00', TIMESTAMP '2026-07-05 11:05:00', TIMESTAMP '2026-07-05 11:10:00', 'ops.user');

