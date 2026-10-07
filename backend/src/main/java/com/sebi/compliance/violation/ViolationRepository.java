package com.sebi.compliance.violation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    Optional<Violation> findByViolationCode(String violationCode);
    List<Violation> findByMemberId(Long memberId);
    List<Violation> findByMemberIdAndStatusIn(Long memberId, List<ViolationStatus> statuses);

    @Query("SELECT v FROM Violation v WHERE " +
           "(:memberId IS NULL OR v.member.id = :memberId) AND " +
           "(:severity IS NULL OR v.severity = :severity) AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:query IS NULL OR LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.violationCode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Violation> findWithFilters(
            @Param("memberId") Long memberId,
            @Param("severity") Severity severity,
            @Param("status") ViolationStatus status,
            @Param("query") String query,
            Pageable pageable
    );

    long countByStatus(ViolationStatus status);
    long countBySeverity(Severity severity);
    long countByMemberIdAndStatusIn(Long memberId, List<ViolationStatus> statuses);
}
