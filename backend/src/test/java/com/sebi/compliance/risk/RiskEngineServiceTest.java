package com.sebi.compliance.risk;

import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberRepository;
import com.sebi.compliance.record.RecordRepository;
import com.sebi.compliance.violation.Severity;
import com.sebi.compliance.violation.Violation;
import com.sebi.compliance.violation.ViolationRepository;
import com.sebi.compliance.violation.ViolationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskEngineServiceTest {

    @Mock private RiskAssessmentRepository riskRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ViolationRepository violationRepository;
    @Mock private RecordRepository recordRepository;

    @InjectMocks private RiskEngineService riskEngineService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setMemberCode("SB-001");
        member.setOrganizationName("Test Broker");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(riskRepository.save(any(RiskAssessment.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Risk score calculation with zero violations should yield LOW risk level")
    void testLowRiskCalculation() {
        when(violationRepository.findByMemberIdAndStatusIn(eq(1L), any())).thenReturn(Collections.emptyList());
        when(recordRepository.findByMemberId(1L)).thenReturn(Collections.emptyList());

        RiskAssessment assessment = riskEngineService.calculateMemberRisk(1L);

        assertNotNull(assessment);
        assertEquals(0.0, assessment.getRiskScore());
        assertEquals(RiskLevel.LOW, assessment.getRiskLevel());
    }

    @Test
    @DisplayName("Risk score with critical active violations should escalate risk score above baseline")
    void testCriticalRiskCalculation() {
        Violation v1 = new Violation();
        v1.setSeverity(Severity.CRITICAL);
        v1.setStatus(ViolationStatus.OPEN);

        Violation v2 = new Violation();
        v2.setSeverity(Severity.CRITICAL);
        v2.setStatus(ViolationStatus.ACTION_REQUIRED);

        when(violationRepository.findByMemberIdAndStatusIn(eq(1L), any())).thenReturn(Arrays.asList(v1, v2));
        when(recordRepository.findByMemberId(1L)).thenReturn(Collections.emptyList());

        RiskAssessment assessment = riskEngineService.calculateMemberRisk(1L);

        assertNotNull(assessment);
        assertTrue(assessment.getRiskScore() >= 30.0, "Risk score should escalate with active critical violations");
        assertNotEquals(RiskLevel.LOW, assessment.getRiskLevel());
    }
}
