package com.europe.sepa.backoffice.repository;

import com.europe.sepa.backoffice.entity.LedgerEntryView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LedgerEntryViewRepository extends JpaRepository<LedgerEntryView, String> {

    Optional<LedgerEntryView> findByPaymentId(String paymentId);

    List<LedgerEntryView> findByCorrelationId(String correlationId);
}

