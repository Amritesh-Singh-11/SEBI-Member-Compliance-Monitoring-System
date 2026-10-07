package com.sebi.compliance.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberRepository;
import com.sebi.compliance.record.ComplianceRecord;
import com.sebi.compliance.record.RecordRepository;
import com.sebi.compliance.record.RecordStatus;
import com.sebi.compliance.violation.Severity;
import com.sebi.compliance.violation.Violation;
import com.sebi.compliance.violation.ViolationRepository;
import com.sebi.compliance.violation.ViolationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RiskEngineService {

    private static final Logger logger = LoggerFactory.getLogger(RiskEngineService.class);

    private final RiskAssessmentRepository riskRepository;
    private final MemberRepository memberRepository;
    private final ViolationRepository violationRepository;
    private final RecordRepository recordRepository;
    private final ObjectMapper objectMapper;

    public RiskEngineService(RiskAssessmentRepository riskRepository, MemberRepository memberRepository,
                             ViolationRepository violationRepository, RecordRepository recordRepository) {
        this.riskRepository = riskRepository;
        this.memberRepository = memberRepository;
        this.violationRepository = violationRepository;
        this.recordRepository = recordRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public RiskAssessment calculateMemberRisk(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));

        List<Violation> openViolations = violationRepository.findByMemberIdAndStatusIn(memberId,
                Arrays.asList(ViolationStatus.OPEN, ViolationStatus.UNDER_REVIEW, ViolationStatus.ACTION_REQUIRED));
        List<ComplianceRecord> allRecords = recordRepository.findByMemberId(memberId);

        // 1. Violation Frequency Factor (30% weight) -> min(100, openViolations * 20)
        double freqScore = Math.min(100.0, openViolations.size() * 20.0);

        // 2. Violation Severity Factor (25% weight)
        double severitySum = 0;
        for (Violation v : openViolations) {
            if (v.getSeverity() == Severity.CRITICAL) severitySum += 40;
            else if (v.getSeverity() == Severity.HIGH) severitySum += 25;
            else if (v.getSeverity() == Severity.MEDIUM) severitySum += 15;
            else severitySum += 5;
        }
        double severityScore = Math.min(100.0, severitySum);

        // 3. Late Submissions Factor (20% weight)
        long overdueCount = allRecords.stream()
                .filter(r -> r.getStatus() == RecordStatus.OVERDUE || (r.getSubmissionDate() != null && r.getSubmissionDate().isAfter(r.getDueDate())))
                .count();
        double lateScore = Math.min(100.0, overdueCount * 25.0);

        // 4. Compliance History Factor (15% weight) -> 100 - complianceRate%
        long compliantCount = allRecords.stream().filter(r -> r.getStatus() == RecordStatus.COMPLIANT).count();
        double compRate = allRecords.isEmpty() ? 100.0 : ((double) compliantCount / allRecords.size()) * 100.0;
        double historyScore = 100.0 - compRate;

        // 5. Repeated Violations Factor (10% weight)
        Map<String, Integer> reqCounts = new HashMap<>();
        for (Violation v : openViolations) {
            if (v.getRule() != null) {
                reqCounts.put(v.getRule().getRuleCode(), reqCounts.getOrDefault(v.getRule().getRuleCode(), 0) + 1);
            }
        }
        long repeatViolations = reqCounts.values().stream().filter(c -> c >= 2).count();
        double repeatScore = Math.min(100.0, repeatViolations * 50.0);

        // Weighted Final Calculation
        double finalScore = (0.30 * freqScore) + (0.25 * severityScore) + (0.20 * lateScore) + (0.15 * historyScore) + (0.10 * repeatScore);
        finalScore = Math.round(finalScore * 10.0) / 10.0; // Round to 1 decimal

        RiskLevel level;
        if (finalScore <= 30.0) level = RiskLevel.LOW;
        else if (finalScore <= 60.0) level = RiskLevel.MEDIUM;
        else if (finalScore <= 80.0) level = RiskLevel.HIGH;
        else level = RiskLevel.CRITICAL;

        // Human-readable Explanations
        List<String> explanations = new ArrayList<>();
        explanations.add(String.format("%d active compliance violations present", openViolations.size()));
        explanations.add(String.format("%d overdue/late regulatory filings in history", overdueCount));
        explanations.add(String.format("Historical filing compliance rate is %.1f%%", compRate));
        if (repeatViolations > 0) {
            explanations.add(String.format("%d repeated violation patterns detected", repeatViolations));
        }

        Map<String, Object> factors = new HashMap<>();
        factors.put("violationFrequencyFactor", freqScore);
        factors.put("violationSeverityFactor", severityScore);
        factors.put("lateSubmissionFactor", lateScore);
        factors.put("complianceHistoryFactor", historyScore);
        factors.put("repeatedViolationFactor", repeatScore);
        factors.put("explanations", explanations);

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(factors);
        } catch (Exception e) {
            jsonPayload = "{}";
        }

        RiskAssessment assessment = new RiskAssessment();
        assessment.setMember(member);
        assessment.setRiskScore(finalScore);
        assessment.setRiskLevel(level);
        assessment.setContributingFactorsJson(jsonPayload);

        RiskAssessment saved = riskRepository.save(assessment);

        // Update member cache fields
        member.setRiskScore(finalScore);
        member.setRiskLevel(level.name());
        memberRepository.save(member);

        logger.info("Recalculated risk score for member {}: Score={}, Level={}", member.getOrganizationName(), finalScore, level);
        return saved;
    }

    public Optional<RiskAssessment> getLatestRiskAssessment(Long memberId) {
        return riskRepository.findTopByMemberIdOrderByCalculatedAtDesc(memberId);
    }
}
