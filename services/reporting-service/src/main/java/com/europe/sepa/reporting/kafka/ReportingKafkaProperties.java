package com.europe.sepa.reporting.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Kafka topic configuration for the reporting service.
 */
@ConfigurationProperties(prefix = "stockholm.kafka.topics")
public class ReportingKafkaProperties {

    private String ledgerUpdated = "ledger.updated";
    private int partitions = 1;
    private short replicationFactor = 1;

    public String getLedgerUpdated() { return ledgerUpdated; }
    public void setLedgerUpdated(String ledgerUpdated) { this.ledgerUpdated = ledgerUpdated; }

    public int getPartitions() { return partitions; }
    public void setPartitions(int partitions) { this.partitions = partitions; }

    public short getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(short replicationFactor) { this.replicationFactor = replicationFactor; }
}

