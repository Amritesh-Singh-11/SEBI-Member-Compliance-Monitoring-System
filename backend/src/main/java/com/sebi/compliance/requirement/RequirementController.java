package com.sebi.compliance.requirement;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.member.MemberType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/requirements")
@Tag(name = "Compliance Requirements Library", description = "Endpoints for managing regulatory compliance requirements")
public class RequirementController {

    private final RequirementService requirementService;

    public RequirementController(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of compliance requirements")
    public ResponseEntity<ApiResponse<PagedResponse<ComplianceRequirement>>> getAllRequirements(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) RequirementCategory category,
            @RequestParam(required = false) MemberType memberType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requirementCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PagedResponse<ComplianceRequirement> paged = requirementService.getAllRequirements(query, category, memberType, active, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Requirements retrieved successfully", paged));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get compliance requirement details by ID")
    public ResponseEntity<ApiResponse<ComplianceRequirement>> getRequirementById(@PathVariable Long id) {
        ComplianceRequirement req = requirementService.getRequirementById(id);
        return ResponseEntity.ok(ApiResponse.success("Requirement details retrieved successfully", req));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Configure new regulatory compliance requirement (Admin only)")
    public ResponseEntity<ApiResponse<ComplianceRequirement>> createRequirement(@Valid @RequestBody ComplianceRequirement requirement) {
        ComplianceRequirement created = requirementService.createRequirement(requirement);
        return ResponseEntity.ok(ApiResponse.success("Compliance requirement created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing compliance requirement (Admin only)")
    public ResponseEntity<ApiResponse<ComplianceRequirement>> updateRequirement(@PathVariable Long id, @Valid @RequestBody ComplianceRequirement requirement) {
        ComplianceRequirement updated = requirementService.updateRequirement(id, requirement);
        return ResponseEntity.ok(ApiResponse.success("Compliance requirement updated successfully", updated));
    }
}
