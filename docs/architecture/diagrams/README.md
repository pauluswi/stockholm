# Architectural Diagrams

This directory contains visual diagrams documenting the architecture of the Stockholm payment platform.

## Diagram Index

| # | Diagram | Type | Coverage |
|---|---------|------|----------|
| [01-context-diagram.md](./01-context-diagram.md) | **Context Diagram** | System Context | External systems, users, interfaces |
| [02-container-diagram.md](./02-container-diagram.md) | **Container Diagram** | Component Structure | Services, databases, infrastructure |
| [03-sequence-diagrams.md](./03-sequence-diagrams.md) | **Sequence Diagrams** | Runtime Behavior | 3 payment flow scenarios |
| [04-deployment-diagram.md](./04-deployment-diagram.md) | **Deployment Diagram** | Infrastructure | Local dev, Docker, Kubernetes, HA |

## Diagram Relationships

```
Context Diagram (System boundaries)
    ↓
    ├─→ Container Diagram (Internal structure)
    │       ↓
    │       ├─→ Sequence Diagrams (Runtime flows)
    │       └─→ Deployment Diagram (How to run)
    │
    └─→ Deployment Diagram (How to run)
```

## C4 Model Structure

The diagrams follow the **C4 Model** approach:

1. **Context** (Level 1): Who uses the system and what external systems it connects to
2. **Container** (Level 2): High-level technology choices (services, databases, brokers)
3. **Component** (Level 3): Not detailed here, but reflected in sequence diagrams
4. **Code** (Level 4): Implementation details in actual source code

## Diagram Technology

All diagrams are created using **Mermaid** syntax for:
- ✅ Version control friendly (stored as text in Git)
- ✅ Platform independent (renders in any markdown viewer)
- ✅ GitHub/GitLab native support
- ✅ Easy to update and maintain
- ✅ Documentation-as-code philosophy

## Quick Links

### For Stakeholders
- **Understand the system**: Start with [Context Diagram](./01-context-diagram.md)
- **See how it works**: Then [Sequence Diagrams](./03-sequence-diagrams.md)

### For Engineers
- **Architecture overview**: [Container Diagram](./02-container-diagram.md)
- **Development setup**: [Deployment Diagram](./04-deployment-diagram.md) - Local Dev section
- **Payment flows**: [Sequence Diagrams](./03-sequence-diagrams.md)

### For DevOps
- **Local development**: [Deployment Diagram](./04-deployment-diagram.md) - Docker Compose section
- **Production**: [Deployment Diagram](./04-deployment-diagram.md) - Production/Kubernetes sections
- **HA/DR**: [Deployment Diagram](./04-deployment-diagram.md) - HA Configuration section

## Viewing These Diagrams

### In GitHub/GitLab
- Click on any `.md` file and Mermaid diagrams render automatically
- ✅ No additional tools needed
- ✅ Works on mobile too

### Locally
- Install Mermaid CLI: `npm install -g @mermaid-js/mermaid-cli`
- Export to PNG: `mmdc -i diagram.md -o diagram.png`

### In IDEs
- IntelliJ IDEA: Install "Markdown" or "Mermaid Diagram" plugin
- VS Code: Install "Markdown Preview Mermaid Support" extension

## Diagram Details

### Context Diagram
**When to use**: First interaction with the system
- Shows Stockholm as a black box
- External systems (clearing, settlement, risk engine)
- User interactions
- Data flows at high level
- **Reference**: arc42 section 3

### Container Diagram
**When to use**: Understanding components
- All microservices visible
- Key infrastructure (Kafka, PostgreSQL, Redis)
- Observability stack (Prometheus, Grafana, Loki, Tempo)
- Communication patterns
- **Reference**: arc42 section 5

### Sequence Diagrams
**When to use**: Understanding complex flows
- **Scenario 1**: Successful payment (happy path)
- **Scenario 2**: High-risk transaction detection and incident management
- **Scenario 3**: Settlement failure with retry and recovery
- State transitions for payments and incidents
- Correlation ID tracking
- **Reference**: arc42 section 6

### Deployment Diagram
**When to use**: Running the system
- **Local Development**: Docker Compose for dev machines
- **Production**: Container orchestration options
- **Kubernetes**: Multi-replica, HA setup
- **DR Configuration**: Multi-region failover
- **Reference**: arc42 section 7

## Keeping Diagrams Updated

### When to Update
- Architecture changes (new services)
- New deployment options
- Process flow changes
- Technology stack updates

### How to Update
1. Edit the markdown file
2. Update Mermaid syntax
3. Verify by viewing in GitHub/GitLab
4. Commit to Git
5. Update references in other docs if needed

### Versioning
- Keep git history (blame functionality)
- Include dates in diagram headers
- Document major changes in commit messages

## Related Documentation

- **README.md**: Project overview
- **arc42.md**: Full architecture documentation (sections 3-7 reference these diagrams)
- **adr/**: Architecture Decision Records (decisions behind the architecture)
- **Source code**: Actual implementation

---

**Last Updated**: June 26, 2026
**Format**: Mermaid diagrams in Markdown
**Audience**: Architects, Engineers, DevOps, Stakeholders

