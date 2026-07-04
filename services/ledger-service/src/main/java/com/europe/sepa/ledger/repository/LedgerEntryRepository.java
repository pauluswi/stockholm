package com.europe.sepa.ledger.repository;

import com.europe.sepa.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA repository for ledger entries.
 */
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, String> {
    Optional<LedgerEntry> findByPaymentId(String paymentId);
}

