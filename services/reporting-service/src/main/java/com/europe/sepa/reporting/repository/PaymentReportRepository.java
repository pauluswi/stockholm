package com.europe.sepa.reporting.repository;

import com.europe.sepa.reporting.entity.PaymentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentReportRepository extends JpaRepository<PaymentReport, String> {

    List<PaymentReport> findByPaymentId(String paymentId);

    Optional<PaymentReport> findByCorrelationId(String correlationId);

    List<PaymentReport> findByStatus(String status);
}

