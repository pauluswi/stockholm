package com.europe.sepa.backoffice.repository;

import com.europe.sepa.backoffice.entity.PaymentReportView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentReportViewRepository extends JpaRepository<PaymentReportView, String> {

    List<PaymentReportView> findByPaymentId(String paymentId);

    List<PaymentReportView> findByCorrelationId(String correlationId);

    long countByStatus(String status);
}

