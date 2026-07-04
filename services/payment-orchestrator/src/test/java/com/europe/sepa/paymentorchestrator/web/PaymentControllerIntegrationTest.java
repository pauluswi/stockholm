package com.europe.sepa.paymentorchestrator.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.europe.sepa.paymentorchestrator.testsupport.KafkaIntegrationTest;
import com.europe.sepa.paymentorchestrator.web.dto.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End integration test for payment initiation.
 * Tests REST endpoint with embedded Kafka.
 */
@KafkaIntegrationTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testInitiatePaymentSuccess() throws Exception {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setOrderer("SENDER123");
        request.setBeneficiary("RECEIVER456");
        request.setAmount(new BigDecimal("250.50"));
        request.setCurrency("EUR");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("initiated"))
                .andExpect(jsonPath("$.correlationId").isNotEmpty());
    }
    
    @Test
    public void testInitiatePaymentValidation() throws Exception {
        // Arrange - Missing beneficiary
        PaymentRequest request = new PaymentRequest();
        request.setOrderer("SENDER123");
        request.setAmount(new BigDecimal("250.50"));
        request.setCurrency("EUR");
        
        // Act & Assert - Should fail validation
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testInitiatePaymentWithDefaultCurrency() throws Exception {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setOrderer("SENDER789");
        request.setBeneficiary("RECEIVER101");
        request.setAmount(new BigDecimal("500.00"));
        // Currency defaults to EUR
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("initiated"));
    }
}

