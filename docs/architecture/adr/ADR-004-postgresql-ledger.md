# ADR-004: PostgreSQL for Ledger and State Management

## Status
Accepted

## Context

Stockholm requires persistent state storage for:

- Payment records and status tracking
- Account balances and ledger entries
- Transaction history and audit trails
- Incident records
- Operational metadata

The ledger must be:

- ACID-compliant for financial accuracy
- Queryable with complex SQL for reporting
- Durable and reliable
- Easy to develop against locally
- Suitable for integration tests

Multiple services read and write to shared data:
- Ledger Service updates balances
- Reporting Service queries for reports
- Backoffice API searches transactions
- Resilience Monitor tracks incidents

## Decision

We have chosen **PostgreSQL** as the primary persistent data store.

PostgreSQL provides:
- ACID transactions ensuring consistency
- Rich query language (SQL) for complex reporting
- JSON support for flexible data structures
- Built-in full-text search
- Proven reliability in financial systems
- Excellent Docker support for local development

## Consequences

### Positive
- **ACID guarantees**: Critical for financial data accuracy
- **SQL expressiveness**: Complex queries for reporting and analysis
- **Transactions**: Multi-statement operations ensure consistency
- **Auditability**: Triggers and audit tables track all changes
- **Scalability**: Can handle millions of transactions
- **Operational maturity**: Battle-tested in production
- **Open source**: No licensing costs
- **Developer productivity**: Rich tooling, widespread knowledge

### Negative
- **Vertical scaling limits**: Eventually requires sharding for very high volume
- **Operational complexity**: Backup, replication, failover require planning
- **Not ideal for time-series**: Could use separate time-series DB for metrics
- **Schema management**: Migrations require careful planning

### Trade-offs
- Chose strong consistency and auditability over NoSQL flexibility
- Prioritized ACID guarantees for financial accuracy

## Alternatives Considered

### MongoDB
- **Pros**: Flexible schema, horizontal scaling
- **Cons**: Weaker ACID in earlier versions, eventual consistency concerns

### MySQL
- **Pros**: Lightweight, widespread
- **Cons**: Less advanced features than PostgreSQL, weaker JSON support

### CockroachDB
- **Pros**: Distributed, ACID
- **Cons**: More complex to operate, overkill for current scale

### NoSQL (DynamoDB, Cassandra)
- **Pros**: Horizontal scaling, high throughput
- **Cons**: Eventual consistency not suitable for ledger, complex querying

### Event Sourcing Only
- **Pros**: Complete audit trail, no separate database
- **Cons**: Query rebuilding complexity, operational overhead

## Decision Drivers

1. **Financial accuracy**: ACID transactions are non-negotiable
2. **Reporting requirements**: Complex SQL queries for payments and incidents
3. **Regulatory concern**: Auditability demands durable, queryable records
4. **Team familiarity**: PostgreSQL widely known in Java ecosystem
5. **Local development**: Docker support critical for showcase

## Schema Strategy

- Payments table: Transaction records with status
- Ledger entries: All balance updates with immutable records
- Incidents: Operational incidents with timeline
- Audit events: Complete change history
- Indexes: Optimized for payment lookups and time-based queries

## Related Decisions

- Works with ADR-002 (Event-Driven Architecture) - Events trigger ledger updates
- Complements ADR-006 (Immutable Audit Trail)
- Enables ADR-007 (Correlation ID Strategy)

