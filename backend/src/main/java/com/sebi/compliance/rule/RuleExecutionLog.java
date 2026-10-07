package com.sebi.compliance.rule;

import com.sebi.compliance.member.Member;
import com.sebi.compliance.record.ComplianceRecord;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rule_execution_logs")
public class RuleExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compliance_record_id")
    private ComplianceRecord complianceRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Boolean triggered;

    @Column(name = "execution_details", columnDefinition = "TEXT")
    private String executionDetails;

    @Column(name = "executed_at", updatable = false)
    private LocalDateTime executedAt = LocalDateTime.now();

    public RuleExecutionLog() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public ComplianceRecord getComplianceRecord() {
        return complianceRecord;
    }

    public void setComplianceRecord(ComplianceRecord complianceRecord) {
        this.complianceRecord = complianceRecord;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Boolean getTriggered() {
        return triggered;
    }

    public void setTriggered(Boolean triggered) {
        this.triggered = triggered;
    }

    public String getExecutionDetails() {
        return executionDetails;
    }

    public void setExecutionDetails(String executionDetails) {
        this.executionDetails = executionDetails;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}
