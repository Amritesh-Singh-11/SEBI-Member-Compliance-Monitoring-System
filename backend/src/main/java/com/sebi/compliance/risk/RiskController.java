package com.sebi.compliance.risk;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.member.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/risk")
@Tag(name = "Risk Assessment Engine", description = "Endpoints for academic explainable risk score calculation and breakdown")
public class RiskController {

    private final RiskEngineService riskEngineService;
    private final RiskAssessmentRepository riskRepository;

    public RiskController(RiskEngineService riskEngineService, RiskAssessmentRepository riskRepository) {
        this.riskEngineService = riskEngineService;
        this.riskRepository = riskRepository;
    }

    @GetMapping
    @Operation(summary = "Get list of all recent member risk assessments")
    public ResponseEntity<ApiResponse<List<RiskAssessment>>> getAllAssessments() {
        return ResponseEntity.ok(ApiResponse.success("Risk assessments retrieved successfully", riskRepository.findAll()));
    }

    @GetMapping({"/members/{memberId}", "/member/{memberId}"})
    @Operation(summary = "Get explainable risk score breakdown and factor analysis for member")
    public ResponseEntity<ApiResponse<RiskAssessment>> getMemberRiskAssessment(@PathVariable Long memberId) {
        RiskAssessment assessment = riskEngineService.getLatestRiskAssessment(memberId)
                .orElseGet(() -> riskEngineService.calculateMemberRisk(memberId));
        return ResponseEntity.ok(ApiResponse.success("Risk assessment retrieved successfully", assessment));
    }

    @PostMapping({"/calculate", "/recalculate/{memberId}"})
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Recalculate member risk score on-demand using deterministic academic engine")
    public ResponseEntity<ApiResponse<RiskAssessment>> calculateMemberRisk(@RequestParam(required = false) Long memberId, @PathVariable(required = false) Long memberIdPath) {
        Long targetId = memberId != null ? memberId : memberIdPath;
        if (targetId == null) {
            targetId = 1L;
        }
        RiskAssessment assessment = riskEngineService.calculateMemberRisk(targetId);
        return ResponseEntity.ok(ApiResponse.success("Risk score recalculated successfully", assessment));
    }
}
