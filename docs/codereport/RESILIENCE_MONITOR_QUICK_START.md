# Quick Start Guide - Stockholm Resilience Monitor

## 🚀 One-Command Deployment (When Docker Available)

```bash
cd /Users/slametwidodo/IdeaProjects/stockholm
docker compose up -d
```

## 📋 Verify Services Running

```bash
# List all services
docker compose ps

# Expected output:
# NAME                           STATUS         PORTS
# stockholm-zookeeper-1         Up            2181:2181
# stockholm-kafka-1             Up            9092:9092
# stockholm-postgres-1          Up            5432:5432
# stockholm-redis-1             Up            6379:6379
# stockholm-anomaly-detection-service-1    Up    8087:8087
# stockholm-reporting-service-1            Up    8086:8086
# stockholm-resilience-monitor-1           Up    8088:8088
```

---

## ✅ Health Checks

### Individual Services
```bash
# Anomaly Detection
curl http://localhost:8087/health

# Reporting Service
curl http://localhost:8086/health

# Resilience Monitor
curl http://localhost:8088/health
```

### Kafka Health
```bash
kafka-topics --bootstrap-server localhost:9092 --list
```

### Database Health
```bash
psql -U postgres -d stockholm -c "SELECT 1"
```

---

## 🎯 Common Operations

### View System Metrics
```bash
curl http://localhost:8088/resilience/metrics | jq
```

**Output:**
```json
{
  "openIncidents": 0,
  "criticalIncidents": 0,
  "dlqMessageCount": 0,
  "dlqThreshold": 100,
  "systemHealth": "HEALTHY"
}
```

### View Open Incidents
```bash
curl http://localhost:8088/resilience/incidents/open | jq
```

### Query Incidents by Payment
```bash
curl http://localhost:8088/resilience/incidents/by-payment/PAY-001 | jq
```

### Acknowledge an Incident
```bash
curl -X POST "http://localhost:8088/resilience/incidents/1/acknowledge?assignedTo=jane.smith"
```

### Resolve an Incident
```bash
curl -X POST http://localhost:8088/resilience/incidents/1/resolve
```

### Monitor DLQ Status
```bash
curl http://localhost:8088/resilience/dlq/status | jq
```

---

## 📊 Real-Time Monitoring

### Stream Service Logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f resilience-monitor

# Follow anomaly detection
docker compose logs -f anomaly-detection-service
```

### Database Query Examples
```bash
psql -U postgres -d stockholm

-- View all incidents
SELECT incident_id, incident_type, severity, status, created_at
FROM incidents ORDER BY created_at DESC;

-- Count incidents by severity
SELECT severity, COUNT(*) FROM incidents GROUP BY severity;

-- View critical incidents
SELECT * FROM incidents WHERE severity = 'CRITICAL';

-- View open incidents
SELECT * FROM incidents WHERE status = 'OPEN';
```

---

## 🔄 Event Flow Walkthrough

### Step 1: Publish Anomaly Event
```bash
# Via Kafka (simulated)
kafka-console-producer --bootstrap-server localhost:9092 --topic anomaly.detected
# Paste JSON: (see below)
```

**Sample Event:**
```json
{
  "eventId": "EVT-ANOM-001",
  "eventType": "anomaly.detected",
  "correlationId": "CORR-12345",
  "timestamp": "2026-07-04T14:30:00Z",
  "paymentId": "PAY-TEST-001",
  "riskScore": 85,
  "severity": "HIGH",
  "reasons": [
    "Very high transaction amount (> €50,000)",
    "New beneficiary (first payment to this party)",
    "Payment outside business hours"
  ]
}
```

### Step 2: Monitor Incident Creation
```bash
# Watch logs
docker compose logs -f resilience-monitor

# Expected log output:
# [anomaly-listener] Received anomaly event: ...
# [resilience] Created ANOMALY incident ANOM-XXXXXXXX for payment PAY-TEST-001
```

### Step 3: Query Created Incident
```bash
curl http://localhost:8088/resilience/incidents/by-payment/PAY-TEST-001 | jq
```

**Response:**
```json
[
  {
    "id": 1,
    "incidentId": "ANOM-A1B2C3D4",
    "paymentId": "PAY-TEST-001",
    "correlationId": "CORR-12345",
    "incidentType": "ANOMALY_DETECTED",
    "severity": "CRITICAL",
    "status": "OPEN",
    "riskScore": 85,
    "details": [
      "Very high transaction amount (> €50,000)",
      "New beneficiary (first payment to this party)",
      "Payment outside business hours"
    ],
    "createdAt": "2026-07-04T14:30:01Z"
  }
]
```

### Step 4: Acknowledge Incident
```bash
curl -X POST "http://localhost:8088/resilience/incidents/1/acknowledge?assignedTo=ops-team"
```

### Step 5: Resolve Incident
```bash
curl -X POST http://localhost:8088/resilience/incidents/1/resolve
```

---

## 🔧 Troubleshooting

### Service Won't Start

**Check Docker Daemon**
```bash
docker ps
# If error: Cannot connect to Docker daemon
# → Start Docker Desktop
```

**Check Port Conflicts**
```bash
lsof -i :8088  # Check if port 8088 is in use
```

**View Build Logs**
```bash
docker compose build --no-cache resilience-monitor 2>&1 | tail -50
```

### No Incidents Being Created

**Verify Kafka Topics Exist**
```bash
kafka-topics --bootstrap-server localhost:9092 --list | grep anomaly
```

**Check Consumer Group**
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --list | grep resilience
```

**View Kafka Messages**
```bash
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic anomaly.detected --from-beginning
```

### Database Connection Issues

**Verify PostgreSQL Running**
```bash
psql -U postgres -h localhost -d stockholm -c "SELECT version();"
```

**Reset Database**
```bash
docker compose exec postgres psql -U postgres -d stockholm -c "DROP TABLE incidents CASCADE;"
docker compose restart resilience-monitor
```

---

## 📈 Performance Testing

### Load Test: Simulate Multiple Incidents

```bash
#!/bin/bash
# simulate_incidents.sh

for i in {1..10}; do
  payload=$(cat <<EOF
{
  "eventId": "EVT-ANOM-$i",
  "eventType": "anomaly.detected",
  "correlationId": "CORR-$i",
  "timestamp": "2026-07-04T14:30:00Z",
  "paymentId": "PAY-$i",
  "riskScore": $((75 + RANDOM % 25)),
  "severity": "HIGH",
  "reasons": ["High amount", "New beneficiary"]
}
EOF
  )

  echo "$payload" | kafka-console-producer \
    --bootstrap-server localhost:9092 \
    --topic anomaly.detected

  echo "Published incident $i"
  sleep 0.5
done

echo "All incidents published"
curl http://localhost:8088/resilience/metrics | jq
```

### Monitor System Performance
```bash
# Check open incidents
watch 'curl -s http://localhost:8088/resilience/metrics | jq ".openIncidents"'

# Check DLQ counter
watch 'curl -s http://localhost:8088/resilience/dlq/status | jq ".messageCount"'
```

---

## 📊 Key Endpoints Reference

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/resilience/incidents/open` | GET | List open incidents |
| `/resilience/incidents/critical` | GET | List critical incidents |
| `/resilience/incidents/by-payment/{id}` | GET | Get incidents for payment |
| `/resilience/incidents/{id}/acknowledge` | POST | Acknowledge incident |
| `/resilience/incidents/{id}/resolve` | POST | Resolve incident |
| `/resilience/metrics` | GET | System health metrics |
| `/resilience/dlq/status` | GET | DLQ overflow status |
| `/resilience/dlq/reset` | POST | Reset DLQ counter |

---

## 🎯 System Health Levels

| Level | Condition | Action |
|-------|-----------|--------|
| 🟢 **HEALTHY** | No open incidents | Normal monitoring |
| 🟡 **WARNING** | 1-5 open incidents | Review trends |
| 🟠 **DEGRADED** | >5 open incidents | Active monitoring |
| 🔴 **CRITICAL** | Critical incidents | Immediate escalation |

---

## 📝 Notes

### Correlation IDs
All events carry the same `correlationId` for full traceability:
```
payment.initiated (CORR-12345)
    ↓
anomaly.detected (CORR-12345)
    ↓
settlement.failed (CORR-12345)
    ↓
Incidents all linked via CORR-12345
```

### DLQ Threshold
- Tracks poison messages with retry count ≥ 3
- Overflow threshold: 100 messages
- Creates CRITICAL escalated incident automatically
- Manual reset via `POST /resilience/dlq/reset`

### Incident Lifecycle
```
Created (OPEN) → Team Review → Assigned (ACKNOWLEDGED) → Resolved
```

---

## 🔗 Related Documentation

- **Anomaly Detection Service**: `services/anomaly-detection-service/README.md`
- **Resilience Monitor Full Guide**: `services/resilience-monitor/README.md`
- **Architecture**: `docs/architecture/arc42.md`
- **Deployment**: `PHASE_3_COMPLETE.md`

---

## 💡 Tips & Tricks

### Quick Incident Count
```bash
curl -s http://localhost:8088/resilience/metrics | jq '.openIncidents'
```

### Watch Metrics in Real-Time
```bash
watch -n 1 'curl -s http://localhost:8088/resilience/metrics | jq'
```

### Export Incidents to CSV
```bash
psql -U postgres -d stockholm -c "COPY (SELECT * FROM incidents) TO STDOUT WITH CSV HEADER;" > incidents.csv
```

### Find Incidents by Date Range
```bash
psql -U postgres -d stockholm << EOF
SELECT incident_id, severity, status
FROM incidents
WHERE created_at >= NOW() - INTERVAL '1 hour'
ORDER BY created_at DESC;
EOF
```

---

## 📞 Support

For issues:
1. Check logs: `docker compose logs -f`
2. Verify connectivity: `curl http://localhost:8088/health`
3. Review README: `services/resilience-monitor/README.md`
4. Check database: `psql -U postgres -d stockholm`

---

**Ready to run! Just execute:**
```bash
docker compose up -d
```

