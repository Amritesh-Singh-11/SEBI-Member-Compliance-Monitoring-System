package com.sebi.compliance.correctiveaction;

import com.sebi.compliance.document.Document;
import com.sebi.compliance.violation.Violation;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "corrective_actions")
public class CorrectiveAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "violation_id", nullable = false)
    private Violation violation;

    @Column(name = "action_code", nullable = false, unique = true, length = 50)
    private String actionCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "responsible_person", length = 150)
    private String responsiblePerson;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evidence_document_id")
    private Document evidenceDocument;

    @Column(name = "verification_remarks", columnDefinition = "TEXT")
    private String verificationRemarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CAStatus status = CAStatus.OPEN;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public CorrectiveAction() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Violation getViolation() {
        return violation;
    }

    public void setViolation(Violation violation) {
        this.violation = violation;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public Document getEvidenceDocument() {
        return evidenceDocument;
    }

    public void setEvidenceDocument(Document evidenceDocument) {
        this.evidenceDocument = evidenceDocument;
    }

    public String getVerificationRemarks() {
        return verificationRemarks;
    }

    public void setVerificationRemarks(String verificationRemarks) {
        this.verificationRemarks = verificationRemarks;
    }

    public CAStatus getStatus() {
        return status;
    }

    public void setStatus(CAStatus status) {
        this.status = status;
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
