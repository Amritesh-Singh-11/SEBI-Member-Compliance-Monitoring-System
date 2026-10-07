package com.sebi.compliance.rule;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rules")
@Tag(name = "Rule Engine Management", description = "Endpoints for configuring dynamic compliance rules and executing automated evaluations")
public class RuleController {

    private final RuleRepository ruleRepository;
    private final RuleEngineService ruleEngineService;

    public RuleController(RuleRepository ruleRepository, RuleEngineService ruleEngineService) {
        this.ruleRepository = ruleRepository;
        this.ruleEngineService = ruleEngineService;
    }

    @GetMapping
    @Operation(summary = "Get list of all active compliance rules")
    public ResponseEntity<ApiResponse<List<Rule>>> getAllRules() {
        List<Rule> rules = ruleRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Rules retrieved successfully", rules));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new configurable compliance rule (Admin only)")
    public ResponseEntity<ApiResponse<Rule>> createRule(@Valid @RequestBody Rule rule) {
        Rule saved = ruleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.success("Rule created successfully", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing compliance rule (Admin only)")
    public ResponseEntity<ApiResponse<Rule>> updateRule(@PathVariable Long id, @Valid @RequestBody Rule updateData) {
        Rule existing = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule", "id", id));
        existing.setRuleName(updateData.getRuleName());
        existing.setDescription(updateData.getDescription());
        existing.setCategory(updateData.getCategory());
        existing.setConditionExpression(updateData.getConditionExpression());
        existing.setActionType(updateData.getActionType());
        existing.setSeverityImpact(updateData.getSeverityImpact());
        existing.setRiskScoreDelta(updateData.getRiskScoreDelta());
        existing.setActive(updateData.getActive());

        Rule saved = ruleRepository.save(existing);
        return ResponseEntity.ok(ApiResponse.success("Rule updated successfully", saved));
    }

    @PostMapping("/execute")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Execute rule engine across all member compliance filings")
    public ResponseEntity<ApiResponse<List<RuleExecutionLog>>> executeRules() {
        List<RuleExecutionLog> logs = ruleEngineService.evaluateAllRules();
        return ResponseEntity.ok(ApiResponse.success("Rule engine evaluation completed successfully", logs));
    }
}
