package com.europe.sepa.resilience.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Kafka topic configuration properties for resilience monitor.
 */
@Component
@ConfigurationProperties(prefix = "stockholm.kafka.topics")
public class ResilienceKafkaProperties {

    private String anomalyDetected = "anomaly.detected";
    private String settlementFailed = "settlement.failed";
    private Integer partitions = 1;
    private Integer replicationFactor = 1;

    public String getAnomalyDetected() { return anomalyDetected; }
    public void setAnomalyDetected(String anomalyDetected) { this.anomalyDetected = anomalyDetected; }

    public String getSettlementFailed() { return settlementFailed; }
    public void setSettlementFailed(String settlementFailed) { this.settlementFailed = settlementFailed; }

    public Integer getPartitions() { return partitions; }
    public void setPartitions(Integer partitions) { this.partitions = partitions; }

    public Integer getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(Integer replicationFactor) { this.replicationFactor = replicationFactor; }
}

