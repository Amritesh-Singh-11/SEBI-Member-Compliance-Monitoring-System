package com.sebi.compliance.risk;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    List<RiskAssessment> findByMemberIdOrderByCalculatedAtDesc(Long memberId);
    Optional<RiskAssessment> findTopByMemberIdOrderByCalculatedAtDesc(Long memberId);
    Page<RiskAssessment> findByMemberId(Long memberId, Pageable pageable);
}
