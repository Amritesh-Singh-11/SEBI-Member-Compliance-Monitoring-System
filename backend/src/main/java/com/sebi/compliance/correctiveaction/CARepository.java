package com.sebi.compliance.correctiveaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CARepository extends JpaRepository<CorrectiveAction, Long> {

    Optional<CorrectiveAction> findByActionCode(String actionCode);
    List<CorrectiveAction> findByViolationId(Long violationId);

    @Query("SELECT ca FROM CorrectiveAction ca WHERE " +
           "(:violationId IS NULL OR ca.violation.id = :violationId) AND " +
           "(:status IS NULL OR ca.status = :status)")
    Page<CorrectiveAction> findWithFilters(
            @Param("violationId") Long violationId,
            @Param("status") CAStatus status,
            Pageable pageable
    );
}
