package com.europe.sepa.settlement.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Kafka topic configuration for the settlement service.
 */
@ConfigurationProperties(prefix = "stockholm.kafka.topics")
public class SettlementKafkaProperties {

    private String paymentInitiated = "payment.initiated";
    private String settlementCompleted = "settlement.completed";
    private String settlementFailed = "settlement.failed";
    private int partitions = 1;
    private short replicationFactor = 1;

    public String getPaymentInitiated() {
        return paymentInitiated;
    }

    public void setPaymentInitiated(String paymentInitiated) {
        this.paymentInitiated = paymentInitiated;
    }

    public String getSettlementCompleted() {
        return settlementCompleted;
    }

    public void setSettlementCompleted(String settlementCompleted) {
        this.settlementCompleted = settlementCompleted;
    }

    public String getSettlementFailed() {
        return settlementFailed;
    }

    public void setSettlementFailed(String settlementFailed) {
        this.settlementFailed = settlementFailed;
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

