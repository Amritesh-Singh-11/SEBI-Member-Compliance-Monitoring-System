package com.sebi.compliance.rule;

import com.sebi.compliance.document.DocumentRepository;
import com.sebi.compliance.member.Member;
import com.sebi.compliance.record.ComplianceRecord;
import com.sebi.compliance.record.RecordRepository;
import com.sebi.compliance.record.RecordStatus;
import com.sebi.compliance.requirement.ComplianceRequirement;
import com.sebi.compliance.requirement.RequirementCategory;
import com.sebi.compliance.violation.ViolationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleEngineServiceTest {

    @Mock private RuleRepository ruleRepository;
    @Mock private RuleExecutionLogRepository executionLogRepository;
    @Mock private RecordRepository recordRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private ViolationRepository violationRepository;

    @InjectMocks private RuleEngineService ruleEngineService;

    private Rule lateSubmissionRule;
    private ComplianceRecord overdueRecord;

    @BeforeEach
    void setUp() {
        lateSubmissionRule = new Rule();
        lateSubmissionRule.setId(1L);
        lateSubmissionRule.setRuleCode("RULE-CMP-001");
        lateSubmissionRule.setRuleName("Late Submission Rule");
        lateSubmissionRule.setCategory(RequirementCategory.REGULATORY_FILING);
        lateSubmissionRule.setConditionExpression("submissionDate > dueDate");
        lateSubmissionRule.setActionType(RuleAction.CREATE_VIOLATION);
        lateSubmissionRule.setSeverityImpact("MEDIUM");

        Member member = new Member();
        member.setId(1L);
        member.setMemberCode("SB-001");
        member.setOrganizationName("ABC Securities");

        ComplianceRequirement req = new ComplianceRequirement();
        req.setId(10L);
        req.setTitle("Cyber Audit Filing");

        overdueRecord = new ComplianceRecord();
        overdueRecord.setId(100L);
        overdueRecord.setMember(member);
        overdueRecord.setRequirement(req);
        overdueRecord.setDueDate(LocalDate.now().minusDays(5));
        overdueRecord.setSubmissionDate(LocalDate.now().minusDays(1)); // Filed late
        overdueRecord.setStatus(RecordStatus.SUBMITTED);

        when(executionLogRepository.save(any(RuleExecutionLog.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("RULE-CMP-001 should trigger violation when filing submission date exceeds due date")
    void testLateSubmissionRuleTriggersViolation() {
        RuleExecutionLog log = ruleEngineService.evaluateRuleOnRecord(lateSubmissionRule, overdueRecord);

        assertTrue(log.getTriggered(), "Log should be marked as triggered for late submission");
        assertEquals(RecordStatus.OVERDUE, overdueRecord.getStatus());
        verify(violationRepository, times(1)).save(any());
        verify(executionLogRepository, times(1)).save(any());
    }
}
