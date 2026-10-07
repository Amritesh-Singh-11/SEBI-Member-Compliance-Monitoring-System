package com.sebi.compliance.regulation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegulationRepository extends JpaRepository<Regulation, Long> {
    Optional<Regulation> findByReferenceNumber(String referenceNumber);
}
