package com.sebi.compliance.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    Optional<Rule> findByRuleCode(String ruleCode);
    List<Rule> findByActiveTrue();
}
