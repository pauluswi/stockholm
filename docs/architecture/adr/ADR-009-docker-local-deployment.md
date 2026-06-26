# ADR-009: Docker-Based Local Deployment

## Status
Accepted

## Context

Stockholm is an educational architecture showcase that must:

- Run completely on developer laptops
- Support multiple operating systems (macOS, Linux, Windows)
- Require minimal setup/configuration
- Enable quick iteration and understanding
- Provide realistic simulated infrastructure
- Support CI/CD integration
- Scale from local dev to Kubernetes production

Infrastructure required:
- Kafka (event backbone)
- PostgreSQL (data store)
- Redis (caching)
- Keycloak (IAM - optional)
- Prometheus (metrics - optional)
- Grafana (dashboards - optional)

Without containerization, developers would need to:
- Install multiple databases
- Manage version compatibility
- Deal with port conflicts
- Struggle with OS-specific installation

## Decision

We have chosen **Docker and Docker Compose** for local development and deployment.

### Deployment Architecture

**Local Development**
```
docker-compose up -d
# Starts:
# - Kafka broker
# - PostgreSQL database
# - Redis cache
# - Keycloak (optional)
# - Prometheus (optional)
# - Grafana (optional)

mvn spring-boot:run
# Runs all microservices locally
```

**Production Ready**
```
docker-compose up
# All services containerized and orchestrated
```

**Kubernetes (Future)**
```
kubectl apply -f k8s/
# Helm charts or Kustomize for orchestration
```

## Consequences

### Positive
- **Consistency**: Same setup across dev, test, production
- **Isolation**: Services run independently, no conflicts
- **Reproducibility**: "Docker run" always produces same result
- **Speed**: Docker Compose up faster than manual installation
- **Cross-platform**: Works on Mac, Linux, Windows identically
- **Clean slate**: Easy to reset by deleting containers
- **Educational**: Students learn containerization patterns
- **CI/CD integration**: GitHub Actions can run Docker easily
- **Resource efficient**: Less overhead than VMs

### Negative
- **Learning curve**: Docker concepts unfamiliar to some developers
- **Debugging**: Harder to inspect container internals
- **Performance**: Slight overhead vs. native processes
- **Disk space**: Docker images consume significant space
- **Network complexity**: Container networking needs understanding
- **M1/ARM support**: Some images need ARM-specific builds

### Trade-offs
- Chose simplicity of setup over native performance
- Accepted Docker overhead for consistency benefits

## Alternatives Considered

### Native Installation
- **Pros**: Maximum performance, full control
- **Cons**: Different setup per OS, version conflicts, tedious

### Virtual Machines (VirtualBox/Vagrant)
- **Pros**: Complete isolation, portable
- **Cons**: Heavy resource use, slow boot, large download sizes

### Cloud-Based (AWS/Azure Dev Environments)
- **Pros**: Always available, powerful
- **Cons**: Costs money, network dependent, not portable

### Kubernetes from Start
- **Pros**: Production-ready from day one
- **Cons**: Complexity too high for local development, steep learning curve

## Decision Drivers

1. **Educational goal**: Showcase modern deployment patterns
2. **Developer experience**: One-command setup is crucial
3. **Reproducibility**: Exact same environment everywhere
4. **Flexibility**: Easy path to Kubernetes later
5. **Industry standard**: Docker widely adopted in financial services

## Docker Compose Structure

### Services Defined

```yaml
version: '3.8'

services:
  # Infrastructure
  kafka:
    image: confluentinc/cp-kafka:latest
    environment:
      KAFKA_BROKER_ID: 1
      # ... configuration

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: stockholm
      POSTGRES_PASSWORD: password

  redis:
    image: redis:7-alpine

  # Optional Services
  keycloak:
    image: quay.io/keycloak/keycloak:latest

  prometheus:
    image: prom/prometheus:latest

  grafana:
    image: grafana/grafana:latest
```

### Startup Sequence

1. `docker-compose build` - Build custom images
2. `docker-compose up -d` - Start infrastructure
3. `mvn clean install` - Build applications
4. `mvn spring-boot:run` - Start services

### Health Checks

Docker Compose health checks verify readiness:

```yaml
postgres:
  healthcheck:
    test: ["CMD", "pg_isready", "-U", "postgres"]
    interval: 10s
    timeout: 5s
    retries: 5
```

Services wait for dependencies before starting.

## Networking

- **Service discovery**: Docker DNS resolution within compose
- **Port mapping**: Expose ports for external access
- **Network isolation**: Custom network for inter-service communication

## Development Workflow

### Day-to-Day

```bash
# Start infrastructure once
docker-compose up -d

# Run services locally (for debugging)
mvn spring-boot:run -pl payment-orchestrator

# Or containerized (production-like)
docker-compose up payment-orchestrator
```

### Testing

```bash
# Integration tests use testcontainers
# Automatically creates Docker containers for each test
mvn verify
```

## Production Considerations

### Container Images

Each service has Dockerfile:
```dockerfile
FROM eclipse-temurin:21-jre-alpine
COPY target/service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Kubernetes Migration Path

Docker images can be directly pushed to Kubernetes:
```bash
docker build -t stockholm/payment-orchestrator .
docker tag stockholm/payment-orchestrator:latest \
  registry.example.com/stockholm/payment-orchestrator:1.0.0
docker push registry.example.com/stockholm/payment-orchestrator:1.0.0

# Then in k8s/payment-orchestrator.yaml
image: registry.example.com/stockholm/payment-orchestrator:1.0.0
```

## Volumes and Persistence

- **Database data**: Mounted volume persists between restarts
- **Kafka data**: Local storage for events
- **Configuration**: ConfigMaps for settings

## Monitoring and Observability

Docker Compose exposes:
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Kafka UI: http://localhost:8080 (optional)

## Documentation

### Getting Started

```bash
# Start everything
docker-compose up -d

# Stop everything
docker-compose down

# View logs
docker-compose logs -f payment-orchestrator

# Reset (delete all data)
docker-compose down -v
```

### Troubleshooting

- Port conflicts: Change exposed ports in docker-compose.yml
- Memory issues: Increase Docker Desktop resources
- Network timeouts: Check service health with `docker-compose ps`
- Permission issues (Linux): Add user to docker group

## Related Decisions

- Complements ADR-001 (Java 21) - Docker images based on Java runtime
- Demonstrates principles from ADR-002 (Event-Driven Architecture)
- Enables testing of ADR-003 (Kafka)
- Supports ADR-004 (PostgreSQL) deployment
- Facilitates local testing of all architectural decisions

