package com.sebi.compliance.rule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleExecutionLogRepository extends JpaRepository<RuleExecutionLog, Long> {
    List<RuleExecutionLog> findByMemberId(Long memberId);
    Page<RuleExecutionLog> findByMemberId(Long memberId, Pageable pageable);
}
