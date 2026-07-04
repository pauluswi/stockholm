package com.europe.sepa.paymentorchestrator.infrastructure.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Kafka topics.
 * Centralizes topic names and configurations.
 */
@Component
@ConfigurationProperties(prefix = "stockholm.kafka.topics")
public class KafkaTopicProperties {
    
    private String paymentInitiated = "payment.initiated";
    private String paymentValidated = "payment.validated";
    private String settlementCompleted = "settlement.completed";
    private String settlementFailed = "settlement.failed";
    private String anomalyDetected = "anomaly.detected";
    private String ledgerUpdated = "ledger.updated";
    private String reportGenerated = "report.generated";
    
    // Standard topic configurations
    private int partitions = 1;
    private short replicationFactor = 1;
    
    // Getters and Setters
    public String getPaymentInitiated() { return paymentInitiated; }
    public void setPaymentInitiated(String paymentInitiated) { this.paymentInitiated = paymentInitiated; }
    
    public String getPaymentValidated() { return paymentValidated; }
    public void setPaymentValidated(String paymentValidated) { this.paymentValidated = paymentValidated; }
    
    public String getSettlementCompleted() { return settlementCompleted; }
    public void setSettlementCompleted(String settlementCompleted) { this.settlementCompleted = settlementCompleted; }
    
    public String getSettlementFailed() { return settlementFailed; }
    public void setSettlementFailed(String settlementFailed) { this.settlementFailed = settlementFailed; }
    
    public String getAnomalyDetected() { return anomalyDetected; }
    public void setAnomalyDetected(String anomalyDetected) { this.anomalyDetected = anomalyDetected; }
    
    public String getLedgerUpdated() { return ledgerUpdated; }
    public void setLedgerUpdated(String ledgerUpdated) { this.ledgerUpdated = ledgerUpdated; }
    
    public String getReportGenerated() { return reportGenerated; }
    public void setReportGenerated(String reportGenerated) { this.reportGenerated = reportGenerated; }
    
    public int getPartitions() { return partitions; }
    public void setPartitions(int partitions) { this.partitions = partitions; }
    
    public short getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(short replicationFactor) { this.replicationFactor = replicationFactor; }
}

