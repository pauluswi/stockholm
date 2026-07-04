package com.europe.sepa.paymentorchestrator.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.europe.sepa.paymentorchestrator.testsupport.KafkaIntegrationTest;
import com.europe.sepa.paymentorchestrator.testsupport.TestKafkaTopics;
import com.europe.sepa.paymentorchestrator.web.dto.PaymentRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final EmbeddedKafkaBroker embeddedKafkaBroker;

    private TestKafkaTopics kafkaTopics;

    @Autowired
    public PaymentControllerIntegrationTest(MockMvc mockMvc,
                                            ObjectMapper objectMapper,
                                            EmbeddedKafkaBroker embeddedKafkaBroker) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.embeddedKafkaBroker = embeddedKafkaBroker;
    }

    @BeforeAll
    void setUpTopics() {
        kafkaTopics = new TestKafkaTopics(embeddedKafkaBroker);
        kafkaTopics.recreateTopics("payment.initiated");
    }

    @AfterAll
    void tearDownTopics() {
        kafkaTopics.deleteTopics("payment.initiated");
    }
    
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

