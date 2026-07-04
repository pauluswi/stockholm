package com.europe.sepa.anomaly.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Kafka topic configuration for the anomaly detection service.
 */
@ConfigurationProperties(prefix = "stockholm.kafka.topics")
public class AnomalyKafkaProperties {

    private String paymentInitiated = "payment.initiated";
    private String anomalyDetected = "anomaly.detected";
    private int partitions = 1;
    private short replicationFactor = 1;

    public String getPaymentInitiated() { return paymentInitiated; }
    public void setPaymentInitiated(String paymentInitiated) { this.paymentInitiated = paymentInitiated; }

    public String getAnomalyDetected() { return anomalyDetected; }
    public void setAnomalyDetected(String anomalyDetected) { this.anomalyDetected = anomalyDetected; }

    public int getPartitions() { return partitions; }
    public void setPartitions(int partitions) { this.partitions = partitions; }

    public short getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(short replicationFactor) { this.replicationFactor = replicationFactor; }
}

