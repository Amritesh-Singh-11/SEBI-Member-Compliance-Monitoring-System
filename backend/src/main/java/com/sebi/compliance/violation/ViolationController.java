package com.sebi.compliance.violation;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/violations")
@Tag(name = "Violation Management", description = "Endpoints for managing regulatory violations and investigation lifecycle")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of compliance violations with severity and member filters")
    public ResponseEntity<ApiResponse<PagedResponse<Violation>>> getAllViolations(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) ViolationStatus status,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "detectedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PagedResponse<Violation> paged = violationService.getAllViolations(memberId, severity, status, query, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Violations retrieved successfully", paged));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get violation details by ID")
    public ResponseEntity<ApiResponse<Violation>> getViolationById(@PathVariable Long id) {
        Violation v = violationService.getViolationById(id);
        return ResponseEntity.ok(ApiResponse.success("Violation details retrieved successfully", v));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Log a new compliance violation")
    public ResponseEntity<ApiResponse<Violation>> createViolation(
            @RequestParam Long memberId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) Severity severity
    ) {
        Violation created = violationService.createViolation(memberId, title, description, severity);
        return ResponseEntity.ok(ApiResponse.success("Violation logged successfully", created));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Assign a compliance officer to investigate violation")
    public ResponseEntity<ApiResponse<Violation>> assignOfficer(@PathVariable Long id, @RequestParam Long officerUserId) {
        Violation updated = violationService.assignOfficer(id, officerUserId);
        return ResponseEntity.ok(ApiResponse.success("Officer assigned successfully", updated));
    }

    @PutMapping("/{id}")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Update violation lifecycle status (Investigating / Resolve / Close)")
    public ResponseEntity<ApiResponse<Violation>> updateStatus(
            @PathVariable Long id,
            @RequestParam ViolationStatus status,
            @RequestParam(required = false) String resolutionRemarks
    ) {
        Violation updated = violationService.updateStatus(id, status, resolutionRemarks);
        return ResponseEntity.ok(ApiResponse.success("Violation status updated to " + status, updated));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Resolve violation ticket")
    public ResponseEntity<ApiResponse<Violation>> resolveViolation(
            @PathVariable Long id,
            @RequestParam(required = false) String resolutionRemarks
    ) {
        Violation updated = violationService.updateStatus(id, ViolationStatus.RESOLVED, resolutionRemarks);
        return ResponseEntity.ok(ApiResponse.success("Violation resolved successfully", updated));
    }
}
