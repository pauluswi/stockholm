# Backoffice API

Operational read API for Stockholm payment operations teams.

## What It Provides

- `GET /backoffice/overview` - aggregate operations metrics
- `GET /backoffice/payments/{paymentId}/timeline` - cross-service timeline for one payment
- `GET /backoffice/correlations/{correlationId}` - trace all rows by correlation id
- `GET /backoffice/incidents` - filter incidents by status/severity
- `GET /backoffice/anomalies/flagged` - list flagged anomalies

The API reads from shared PostgreSQL tables produced by other services:

- `payment_reports`
- `ledger_entries`
- `anomaly_scores`
- `incidents`

## Run Locally

```bash
cd /Users/slametwidodo/IdeaProjects/stockholm/services/backoffice-api
/Users/slametwidodo/.m2/wrapper/dists/apache-maven-3.9.12/6068d197/bin/mvn spring-boot:run
```

## Run Tests

```bash
cd /Users/slametwidodo/IdeaProjects/stockholm/services/backoffice-api
/Users/slametwidodo/.m2/wrapper/dists/apache-maven-3.9.12/6068d197/bin/mvn test
```

## Notes

- Current security config is open for demo use (`/backoffice/**` permitted).
- Replace with OAuth2/JWT integration for production use.

