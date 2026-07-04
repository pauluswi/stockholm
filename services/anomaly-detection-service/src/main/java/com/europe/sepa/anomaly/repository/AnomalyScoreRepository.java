package com.europe.sepa.anomaly.repository;

import com.europe.sepa.anomaly.entity.AnomalyScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnomalyScoreRepository extends JpaRepository<AnomalyScore, String> {

    Optional<AnomalyScore> findByPaymentId(String paymentId);

    List<AnomalyScore> findByFlaggedTrue();

    List<AnomalyScore> findBySeverity(String severity);
}

