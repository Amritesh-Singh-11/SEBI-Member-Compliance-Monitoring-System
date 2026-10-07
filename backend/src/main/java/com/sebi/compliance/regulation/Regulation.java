package com.sebi.compliance.regulation;

import com.sebi.compliance.member.MemberType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "regulations")
public class Regulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "reference_number", nullable = false, unique = true, length = 100)
    private String referenceNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_member_type", nullable = false, length = 50)
    private MemberType applicableMemberType = MemberType.STOCK_BROKER;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(nullable = false, length = 20)
    private String version = "1.0";

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    public Regulation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public MemberType getApplicableMemberType() {
        return applicableMemberType;
    }

    public void setApplicableMemberType(MemberType applicableMemberType) {
        this.applicableMemberType = applicableMemberType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
