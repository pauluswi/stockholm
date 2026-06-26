package com.europe.sepa.paymentorchestrator.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequest {
    @NotBlank
    private String orderer;
    @NotBlank
    private String beneficiary;
    @NotNull
    private BigDecimal amount;
    private String currency = "EUR";

    public String getOrderer() { return orderer; }
    public void setOrderer(String orderer) { this.orderer = orderer; }
    public String getBeneficiary() { return beneficiary; }
    public void setBeneficiary(String beneficiary) { this.beneficiary = beneficiary; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

