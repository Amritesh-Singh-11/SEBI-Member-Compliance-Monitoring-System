package com.sebi.compliance.requirement;

import com.sebi.compliance.member.MemberType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_requirements")
public class ComplianceRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requirement_code", nullable = false, unique = true, length = 50)
    private String requirementCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RequirementCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Frequency frequency;

    @Column(name = "due_date_offset")
    private Integer dueDateOffset = 0;

    @Column(nullable = false, length = 30)
    private String severity = "MEDIUM";

    @Column(nullable = false)
    private Boolean mandatory = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_member_type", nullable = false, length = 50)
    private MemberType applicableMemberType = MemberType.STOCK_BROKER;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "regulatory_reference", length = 200)
    private String regulatoryReference;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public ComplianceRequirement() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequirementCode() {
        return requirementCode;
    }

    public void setRequirementCode(String requirementCode) {
        this.requirementCode = requirementCode;
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

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Integer getDueDateOffset() {
        return dueDateOffset;
    }

    public void setDueDateOffset(Integer dueDateOffset) {
        this.dueDateOffset = dueDateOffset;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public MemberType getApplicableMemberType() {
        return applicableMemberType;
    }

    public void setApplicableMemberType(MemberType applicableMemberType) {
        this.applicableMemberType = applicableMemberType;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getRegulatoryReference() {
        return regulatoryReference;
    }

    public void setRegulatoryReference(String regulatoryReference) {
        this.regulatoryReference = regulatoryReference;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
