package com.europe.sepa.ledger.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Kafka topic configuration for the ledger service.
 */
@ConfigurationProperties(prefix = "stockholm.kafka.topics")
public class LedgerKafkaProperties {

    private String settlementCompleted = "settlement.completed";
    private String ledgerUpdated = "ledger.updated";
    private String ledgerFailed = "ledger.failed";
    private int partitions = 1;
    private short replicationFactor = 1;

    public String getSettlementCompleted() {
        return settlementCompleted;
    }

    public void setSettlementCompleted(String settlementCompleted) {
        this.settlementCompleted = settlementCompleted;
    }

    public String getLedgerUpdated() {
        return ledgerUpdated;
    }

    public void setLedgerUpdated(String ledgerUpdated) {
        this.ledgerUpdated = ledgerUpdated;
    }

    public String getLedgerFailed() {
        return ledgerFailed;
    }

    public void setLedgerFailed(String ledgerFailed) {
        this.ledgerFailed = ledgerFailed;
    }

    public int getPartitions() {
        return partitions;
    }

    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    public short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
}

