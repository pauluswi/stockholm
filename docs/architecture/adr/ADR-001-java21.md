# ADR-001: Java 21 as Primary Language

## Status
Accepted

## Context

Stockholm is an educational software architecture showcase designed to demonstrate modern enterprise payment processing patterns. The project needs a language and runtime that:

- Supports modern cloud-native architectures
- Integrates seamlessly with Spring Boot ecosystem
- Provides strong typing and maintainability
- Offers excellent performance and scalability
- Has mature tooling and wide industry adoption
- Supports long-term support (LTS) cycles

## Decision

We have chosen **Java 21** as the primary implementation language.

Java 21 is an LTS (Long-Term Support) release offering:
- Virtual threads (Project Loom) for efficient async processing
- Pattern matching for cleaner code
- Record classes for immutable data structures
- Strong ecosystem support via Spring Boot

## Consequences

### Positive
- **Enterprise stability**: LTS release with 8+ years of support
- **Spring Boot alignment**: Optimal integration with Spring ecosystem
- **Performance**: Excellent throughput and latency characteristics
- **Team familiarity**: Industry standard for enterprise Java development
- **Virtual threads**: Efficient handling of thousands of concurrent payment operations
- **Tooling**: Mature IDEs, debuggers, profilers
- **Observability**: Strong support for metrics, tracing, logging

### Negative
- **JVM startup time**: Not ideal for serverless architectures (mitigated with containerization)
- **Memory footprint**: Heavier than Go or Rust for simple services
- **Learning curve**: New developers must learn Java ecosystem

### Trade-offs
- Chose reliability and feature richness over lightweight/minimal footprint
- Prioritized enterprise ecosystem support over cutting-edge language innovation

## Alternatives Considered

### Go
- **Pros**: Fast startup, lightweight, simple concurrency
- **Cons**: Less mature financial systems ecosystem, fewer audit/compliance patterns

### Python
- **Pros**: Easy to learn, rapid development
- **Cons**: Performance limitations for payment processing, GIL constraints, weaker typing

### Rust
- **Pros**: Memory safety, high performance
- **Cons**: Steep learning curve, slower development, smaller financial systems community

### Node.js/TypeScript
- **Pros**: JavaScript ecosystem, ease of development
- **Cons**: Less suitable for payment systems, performance concerns at scale

## Decision Drivers

1. **Educational value**: Java/Spring Boot widely used in enterprise banking
2. **Platform maturity**: Java ecosystem proven in financial services
3. **Team productivity**: Spring Boot reduces boilerplate and accelerates development
4. **Operational experience**: Extensive patterns available for resilience and observability

