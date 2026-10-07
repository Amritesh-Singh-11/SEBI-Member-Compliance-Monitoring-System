package com.sebi.compliance.dashboard;

import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberDTO;
import com.sebi.compliance.member.MemberRepository;
import com.sebi.compliance.member.MemberService;
import com.sebi.compliance.member.MemberStatus;
import com.sebi.compliance.record.ComplianceRecord;
import com.sebi.compliance.record.RecordRepository;
import com.sebi.compliance.record.RecordStatus;
import com.sebi.compliance.violation.Severity;
import com.sebi.compliance.violation.Violation;
import com.sebi.compliance.violation.ViolationRepository;
import com.sebi.compliance.violation.ViolationStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;
    private final ViolationRepository violationRepository;
    private final MemberService memberService;

    public DashboardService(MemberRepository memberRepository, RecordRepository recordRepository,
                            ViolationRepository violationRepository, MemberService memberService) {
        this.memberRepository = memberRepository;
        this.recordRepository = recordRepository;
        this.violationRepository = violationRepository;
        this.memberService = memberService;
    }

    public DashboardMetricsDTO getMetrics() {
        DashboardMetricsDTO metrics = new DashboardMetricsDTO();

        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(MemberStatus.ACTIVE);
        long compliantCount = recordRepository.countByStatus(RecordStatus.COMPLIANT);
        long pendingCount = recordRepository.countByStatus(RecordStatus.PENDING);
        long overdueCount = recordRepository.countOverdueRecords(LocalDate.now());

        long openViolations = violationRepository.countByStatus(ViolationStatus.OPEN) +
                              violationRepository.countByStatus(ViolationStatus.ACTION_REQUIRED) +
                              violationRepository.countByStatus(ViolationStatus.UNDER_REVIEW);

        long highRiskCount = memberRepository.countByRiskLevel("HIGH") + memberRepository.countByRiskLevel("CRITICAL");

        long totalRecords = recordRepository.count();
        double complianceRate = totalRecords == 0 ? 100.0 : Math.round(((double) compliantCount / totalRecords) * 100.0 * 10.0) / 10.0;

        metrics.setTotalMembers(totalMembers);
        metrics.setActiveMembers(activeMembers);
        metrics.setCompliantMembers(compliantCount);
        metrics.setPendingComplianceCount(pendingCount);
        metrics.setOverdueRequirementsCount(overdueCount);
        metrics.setOpenViolationsCount(openViolations);
        metrics.setHighCriticalRiskMembersCount(highRiskCount);
        metrics.setOverallComplianceRate(complianceRate);

        // Compliance status distribution
        Map<String, Long> statusDist = new LinkedHashMap<>();
        for (RecordStatus status : RecordStatus.values()) {
            statusDist.put(status.name(), recordRepository.countByStatus(status));
        }
        metrics.setComplianceStatusDistribution(statusDist);

        // Risk level distribution
        Map<String, Long> riskDist = new LinkedHashMap<>();
        riskDist.put("LOW", memberRepository.countByRiskLevel("LOW"));
        riskDist.put("MEDIUM", memberRepository.countByRiskLevel("MEDIUM"));
        riskDist.put("HIGH", memberRepository.countByRiskLevel("HIGH"));
        riskDist.put("CRITICAL", memberRepository.countByRiskLevel("CRITICAL"));
        metrics.setRiskLevelDistribution(riskDist);

        // Violations by severity
        Map<String, Long> sevDist = new LinkedHashMap<>();
        for (Severity sev : Severity.values()) {
            sevDist.put(sev.name(), violationRepository.countBySeverity(sev));
        }
        metrics.setViolationsBySeverity(sevDist);

        // Violations by category
        List<Violation> allViolations = violationRepository.findAll();
        Map<String, Long> catDist = new LinkedHashMap<>();
        for (Violation v : allViolations) {
            String cat = v.getCategory() != null ? v.getCategory().name() : "OTHER";
            catDist.put(cat, catDist.getOrDefault(cat, 0L) + 1);
        }
        metrics.setViolationsByCategory(catDist);

        // Top High Risk Members
        List<Member> topHighRisk = memberRepository.findWithFilters(null, null, null, "HIGH", PageRequest.of(0, 5, Sort.by("riskScore").descending())).getContent();
        List<Member> topCritical = memberRepository.findWithFilters(null, null, null, "CRITICAL", PageRequest.of(0, 5, Sort.by("riskScore").descending())).getContent();
        List<Member> combinedRisk = new ArrayList<>(topCritical);
        combinedRisk.addAll(topHighRisk);

        List<MemberDTO> riskDtos = combinedRisk.stream().limit(5).map(memberService::mapToDTO).collect(Collectors.toList());
        metrics.setTopHighRiskMembers(riskDtos);

        return metrics;
    }
}
