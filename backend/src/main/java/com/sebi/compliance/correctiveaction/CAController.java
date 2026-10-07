package com.sebi.compliance.correctiveaction;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/corrective-actions")
@Tag(name = "Corrective Actions (CAPA)", description = "Endpoints for managing Corrective and Preventive Actions for compliance violations")
public class CAController {

    private final CAService caService;

    public CAController(CAService caService) {
        this.caService = caService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of Corrective Actions (CAPA)")
    public ResponseEntity<ApiResponse<PagedResponse<CorrectiveAction>>> getAllActions(
            @RequestParam(required = false) Long violationId,
            @RequestParam(required = false) CAStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PagedResponse<CorrectiveAction> paged = caService.getAllActions(violationId, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Corrective actions retrieved successfully", paged));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Issue a new Corrective Action plan for a violation")
    public ResponseEntity<ApiResponse<CorrectiveAction>> createAction(
            @RequestParam Long violationId,
            @RequestParam String description,
            @RequestParam(required = false) String rootCause,
            @RequestParam(required = false) String responsiblePerson,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate
    ) {
        CorrectiveAction created = caService.createAction(violationId, description, rootCause, responsiblePerson, targetDate);
        return ResponseEntity.ok(ApiResponse.success("Corrective Action created successfully", created));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Verify and complete or reject a submitted CAPA evidence plan")
    public ResponseEntity<ApiResponse<CorrectiveAction>> verifyAction(
            @PathVariable Long id,
            @RequestParam CAStatus status,
            @RequestParam(required = false) String verificationRemarks
    ) {
        CorrectiveAction updated = caService.verifyAction(id, status, verificationRemarks);
        return ResponseEntity.ok(ApiResponse.success("CAPA verified successfully and updated to " + status, updated));
    }
}
