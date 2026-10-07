package com.sebi.compliance.violation;

import com.sebi.compliance.member.Member;
import com.sebi.compliance.record.ComplianceRecord;
import com.sebi.compliance.requirement.RequirementCategory;
import com.sebi.compliance.rule.Rule;
import com.sebi.compliance.user.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "violations")
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "violation_code", nullable = false, unique = true, length = 50)
    private String violationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compliance_record_id")
    private ComplianceRecord complianceRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RequirementCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ViolationStatus status = ViolationStatus.OPEN;

    @Column(name = "detected_at", updatable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "resolution_date")
    private LocalDate resolutionDate;

    @Column(name = "resolution_remarks", columnDefinition = "TEXT")
    private String resolutionRemarks;

    public Violation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViolationCode() {
        return violationCode;
    }

    public void setViolationCode(String violationCode) {
        this.violationCode = violationCode;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public ComplianceRecord getComplianceRecord() {
        return complianceRecord;
    }

    public void setComplianceRecord(ComplianceRecord complianceRecord) {
        this.complianceRecord = complianceRecord;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public ViolationStatus getStatus() {
        return status;
    }

    public void setStatus(ViolationStatus status) {
        this.status = status;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public User getAssignedOfficer() {
        return assignedOfficer;
    }

    public void setAssignedOfficer(User assignedOfficer) {
        this.assignedOfficer = assignedOfficer;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDate resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getResolutionRemarks() {
        return resolutionRemarks;
    }

    public void setResolutionRemarks(String resolutionRemarks) {
        this.resolutionRemarks = resolutionRemarks;
    }
}
