package com.sebi.compliance.rule;

import com.sebi.compliance.requirement.RequirementCategory;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_code", nullable = false, unique = true, length = 50)
    private String ruleCode;

    @Column(name = "rule_name", nullable = false, length = 150)
    private String ruleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RequirementCategory category;

    @Column(name = "condition_expression", nullable = false, columnDefinition = "TEXT")
    private String conditionExpression;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private RuleAction actionType = RuleAction.CREATE_VIOLATION;

    @Column(name = "severity_impact", nullable = false, length = 30)
    private String severityImpact = "MEDIUM";

    @Column(name = "risk_score_delta")
    private Double riskScoreDelta = 10.0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Rule() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequirementCategory getCategory() {
        return category;
    }

    public void setCategory(RequirementCategory category) {
        this.category = category;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public RuleAction getActionType() {
        return actionType;
    }

    public void setActionType(RuleAction actionType) {
        this.actionType = actionType;
    }

    public String getSeverityImpact() {
        return severityImpact;
    }

    public void setSeverityImpact(String severityImpact) {
        this.severityImpact = severityImpact;
    }

    public Double getRiskScoreDelta() {
        return riskScoreDelta;
    }

    public void setRiskScoreDelta(Double riskScoreDelta) {
        this.riskScoreDelta = riskScoreDelta;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
