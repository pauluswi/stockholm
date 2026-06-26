package com.europe.sepa.paymentorchestrator.web.dto;

public class PaymentResponse {
    private String paymentId;
    private String status;
    private String correlationId;

    public PaymentResponse() {}
    public PaymentResponse(String paymentId, String status, String correlationId) {
        this.paymentId = paymentId;
        this.status = status;
        this.correlationId = correlationId;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}

