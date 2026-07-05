package com.europe.sepa.backoffice.repository;

import com.europe.sepa.backoffice.entity.AnomalyScoreView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnomalyScoreViewRepository extends JpaRepository<AnomalyScoreView, String> {

    List<AnomalyScoreView> findByPaymentId(String paymentId);

    List<AnomalyScoreView> findByCorrelationId(String correlationId);

    long countByFlaggedTrue();

    @Query("select a from AnomalyScoreView a where a.flagged = true and (:severity is null or a.severity = :severity) order by a.scoredAt desc")
    Page<AnomalyScoreView> findFlagged(@Param("severity") String severity, Pageable pageable);
}

