package com.europe.sepa.backoffice.repository;

import com.europe.sepa.backoffice.entity.IncidentView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentViewRepository extends JpaRepository<IncidentView, Long> {

    List<IncidentView> findByPaymentId(String paymentId);

    List<IncidentView> findByCorrelationId(String correlationId);

    long countByStatus(String status);

    long countBySeverity(String severity);

    @Query("select i from IncidentView i where (:status is null or i.status = :status) and (:severity is null or i.severity = :severity) order by i.createdAt desc")
    Page<IncidentView> search(@Param("status") String status,
                              @Param("severity") String severity,
                              Pageable pageable);
}

