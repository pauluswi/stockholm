package com.europe.sepa.resilience.repository;

import com.europe.sepa.resilience.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting and querying incidents.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Optional<Incident> findByIncidentId(String incidentId);

    List<Incident> findByPaymentId(String paymentId);

    List<Incident> findByStatus(String status);

    List<Incident> findBySeverity(String severity);

    List<Incident> findByIncidentType(String incidentType);

    List<Incident> findByStatusAndSeverity(String status, String severity);

    List<Incident> findByStatusOrderByCreatedAtDesc(String status);
}

