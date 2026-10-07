package com.sebi.compliance.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<ComplianceRecord, Long> {

    Optional<ComplianceRecord> findByMemberIdAndRequirementIdAndPeriodStartAndPeriodEnd(
            Long memberId, Long requirementId, LocalDate periodStart, LocalDate periodEnd
    );

    List<ComplianceRecord> findByMemberId(Long memberId);

    @Query("SELECT cr FROM ComplianceRecord cr WHERE " +
           "(:memberId IS NULL OR cr.member.id = :memberId) AND " +
           "(:requirementId IS NULL OR cr.requirement.id = :requirementId) AND " +
           "(:status IS NULL OR cr.status = :status)")
    Page<ComplianceRecord> findWithFilters(
            @Param("memberId") Long memberId,
            @Param("requirementId") Long requirementId,
            @Param("status") RecordStatus status,
            Pageable pageable
    );

    long countByStatus(RecordStatus status);

    @Query("SELECT COUNT(cr) FROM ComplianceRecord cr WHERE cr.dueDate < :today AND cr.status IN ('PENDING', 'OVERDUE')")
    long countOverdueRecords(@Param("today") LocalDate today);

    @Query("SELECT cr FROM ComplianceRecord cr WHERE cr.dueDate < :today AND cr.status = 'PENDING'")
    List<ComplianceRecord> findNewlyOverdueRecords(@Param("today") LocalDate today);
}
