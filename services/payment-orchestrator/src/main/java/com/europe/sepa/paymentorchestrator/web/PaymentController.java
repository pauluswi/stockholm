package com.europe.sepa.paymentorchestrator.web;

import com.europe.sepa.paymentorchestrator.domain.event.PaymentInitiatedEvent;
import com.europe.sepa.paymentorchestrator.infrastructure.kafka.EventPublisher;
import com.europe.sepa.paymentorchestrator.web.dto.PaymentRequest;
import com.europe.sepa.paymentorchestrator.web.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final EventPublisher eventPublisher;
    
    public PaymentController(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Validated @RequestBody PaymentRequest req) {
        String paymentId = "PAY-" + UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();

        log.info("Payment initiated: paymentId={}, correlationId={}, amount={}", 
                 paymentId, correlationId, req.getAmount());

        // Publish PaymentInitiatedEvent to Kafka
        PaymentInitiatedEvent event = new PaymentInitiatedEvent(
                correlationId, paymentId,
                req.getOrderer(), req.getBeneficiary(),
                req.getAmount(), req.getCurrency()
        );
        
        eventPublisher.publish(event);
        log.debug("PaymentInitiatedEvent published: {}", event);

        PaymentResponse resp = new PaymentResponse(paymentId, "initiated", correlationId);
        return ResponseEntity.status(201).body(resp);
    }
}

