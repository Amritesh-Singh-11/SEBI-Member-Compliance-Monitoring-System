package com.sebi.compliance.risk;

import com.sebi.compliance.member.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "risk_score", nullable = false)
    private Double riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 30)
    private RiskLevel riskLevel;

    @Column(name = "calculated_at", updatable = false)
    private LocalDateTime calculatedAt = LocalDateTime.now();

    @Column(name = "calculation_version", length = 20)
    private String calculationVersion = "1.0-academic";

    @Column(name = "contributing_factors_json", columnDefinition = "TEXT")
    private String contributingFactorsJson;

    public RiskAssessment() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getCalculationVersion() {
        return calculationVersion;
    }

    public void setCalculationVersion(String calculationVersion) {
        this.calculationVersion = calculationVersion;
    }

    public String getContributingFactorsJson() {
        return contributingFactorsJson;
    }

    public void setContributingFactorsJson(String contributingFactorsJson) {
        this.contributingFactorsJson = contributingFactorsJson;
    }
}
