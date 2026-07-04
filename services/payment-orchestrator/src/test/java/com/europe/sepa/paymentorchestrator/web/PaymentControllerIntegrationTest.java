package com.europe.sepa.paymentorchestrator.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.europe.sepa.paymentorchestrator.web.dto.PaymentRequest;
import com.europe.sepa.paymentorchestrator.web.dto.PaymentResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End integration test for payment initiation.
 * Tests REST endpoint with embedded Kafka.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.fail-fast=false",
        "spring.kafka.admin.auto-create-topics=false",
        "stockholm.kafka.create-topics=false"
})
@Disabled("Integration tests disabled temporarily to avoid embedded Kafka bootstrap issues")
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
                .andExpect(jsonPath("$.correlationId").isNotEmpty())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("✓ Payment initiated successfully:");
                    System.out.println("  Response: " + responseBody);
                    
                    PaymentResponse response = objectMapper.readValue(responseBody, PaymentResponse.class);
                    System.out.println("  Payment ID: " + response.getPaymentId());
                    System.out.println("  Correlation ID: " + response.getCorrelationId());
                    System.out.println("  Status: " + response.getStatus());
                });
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
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    System.out.println("✓ Validation correctly rejected invalid payment:");
                    System.out.println("  Error: " + result.getResponse().getContentAsString());
                });
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
                .andExpect(jsonPath("$.status").value("initiated"))
                .andDo(result -> {
                    System.out.println("✓ Payment with default currency EUR initiated successfully");
                });
    }
}

