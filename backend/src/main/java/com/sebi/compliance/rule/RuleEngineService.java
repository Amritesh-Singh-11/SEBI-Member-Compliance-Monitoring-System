package com.sebi.compliance.rule;

import com.sebi.compliance.document.Document;
import com.sebi.compliance.document.DocumentRepository;
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

import java.time.LocalDate;
import java.util.*;

@Service
public class RuleEngineService {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

    private final RuleRepository ruleRepository;
    private final RuleExecutionLogRepository executionLogRepository;
    private final RecordRepository recordRepository;
    private final DocumentRepository documentRepository;
    private final ViolationRepository violationRepository;

    public RuleEngineService(RuleRepository ruleRepository, RuleExecutionLogRepository executionLogRepository,
                             RecordRepository recordRepository, DocumentRepository documentRepository,
                             ViolationRepository violationRepository) {
        this.ruleRepository = ruleRepository;
        this.executionLogRepository = executionLogRepository;
        this.recordRepository = recordRepository;
        this.documentRepository = documentRepository;
        this.violationRepository = violationRepository;
    }

    @Transactional
    public List<RuleExecutionLog> evaluateAllRules() {
        List<Rule> activeRules = ruleRepository.findByActiveTrue();
        List<ComplianceRecord> records = recordRepository.findAll();
        List<RuleExecutionLog> logs = new ArrayList<>();

        for (ComplianceRecord record : records) {
            for (Rule rule : activeRules) {
                RuleExecutionLog log = evaluateRuleOnRecord(rule, record);
                logs.add(log);
            }
        }
        return logs;
    }

    @Transactional
    public RuleExecutionLog evaluateRuleOnRecord(Rule rule, ComplianceRecord record) {
        boolean triggered = false;
        String explanation = "";
        LocalDate today = LocalDate.now();

        switch (rule.getRuleCode()) {
            case "RULE-CMP-001": // Late Submission
                if (record.getSubmissionDate() != null && record.getSubmissionDate().isAfter(record.getDueDate())) {
                    triggered = true;
                    explanation = String.format("Rule %s triggered: Submission date (%s) was after prescribed due date (%s).",
                            rule.getRuleCode(), record.getSubmissionDate(), record.getDueDate());
                    record.setStatus(RecordStatus.OVERDUE);
                } else if (record.getSubmissionDate() == null && today.isAfter(record.getDueDate()) && record.getStatus() != RecordStatus.COMPLIANT) {
                    triggered = true;
                    explanation = String.format("Rule %s triggered: Current date (%s) exceeds due date (%s) with filing pending.",
                            rule.getRuleCode(), today, record.getDueDate());
                    record.setStatus(RecordStatus.OVERDUE);
                }
                break;

            case "RULE-CMP-002": // Mandatory Document Missing
                if (Boolean.TRUE.equals(record.getRequirement().getMandatory()) && record.getStatus() == RecordStatus.SUBMITTED) {
                    List<Document> docs = documentRepository.findByComplianceRecordId(record.getId());
                    if (docs.isEmpty()) {
                        triggered = true;
                        explanation = String.format("Rule %s triggered: Requirement '%s' requires mandatory document proof, but none attached.",
                                rule.getRuleCode(), record.getRequirement().getTitle());
                    }
                }
                break;

            case "RULE-CMP-003": // Expired Document
                List<Document> docs = documentRepository.findByComplianceRecordId(record.getId());
                for (Document doc : docs) {
                    if (doc.getExpiryDate() != null && doc.getExpiryDate().isBefore(today)) {
                        triggered = true;
                        explanation = String.format("Rule %s triggered: Document '%s' expired on %s.",
                                rule.getRuleCode(), doc.getOriginalFilename(), doc.getExpiryDate());
                        break;
                    }
                }
                break;

            case "RULE-CMP-004": // Repeated Violation Recurrence
                List<Violation> pastViolations = violationRepository.findByMemberId(record.getMember().getId());
                long sameRequirementViolations = pastViolations.stream()
                        .filter(v -> v.getComplianceRecord() != null && v.getComplianceRecord().getRequirement().getId().equals(record.getRequirement().getId()))
                        .count();
                if (sameRequirementViolations >= 2) {
                    triggered = true;
                    explanation = String.format("Rule %s triggered: Member '%s' incurred %d repeated violations for requirement '%s'.",
                            rule.getRuleCode(), record.getMember().getOrganizationName(), sameRequirementViolations, record.getRequirement().getTitle());
                }
                break;

            default:
                explanation = "Standard rule evaluation executed.";
                break;
        }

        if (triggered && rule.getActionType() == RuleAction.CREATE_VIOLATION) {
            createViolationIfNotExist(rule, record, explanation);
        }

        RuleExecutionLog log = new RuleExecutionLog();
        log.setRule(rule);
        log.setComplianceRecord(record);
        log.setMember(record.getMember());
        log.setTriggered(triggered);
        log.setExecutionDetails(triggered ? explanation : "Condition evaluated to FALSE.");

        return executionLogRepository.save(log);
    }

    private void createViolationIfNotExist(Rule rule, ComplianceRecord record, String explanation) {
        String violationCode = "VIOL-" + record.getMember().getMemberCode() + "-" + rule.getRuleCode() + "-" + record.getId();
        if (violationRepository.findByViolationCode(violationCode).isEmpty()) {
            Violation v = new Violation();
            v.setViolationCode(violationCode);
            v.setMember(record.getMember());
            v.setComplianceRecord(record);
            v.setRule(rule);
            v.setTitle("Violation: " + rule.getRuleName());
            v.setDescription(explanation);
            v.setCategory(rule.getCategory());
            v.setSeverity(parseSeverity(rule.getSeverityImpact()));
            v.setStatus(ViolationStatus.OPEN);
            v.setDueDate(LocalDate.now().plusDays(15));

            violationRepository.save(v);
            logger.info("Created violation {} for member {}", violationCode, record.getMember().getOrganizationName());
        }
    }

    private Severity parseSeverity(String severityStr) {
        try {
            return Severity.valueOf(severityStr.toUpperCase());
        } catch (Exception e) {
            return Severity.MEDIUM;
        }
    }
}
