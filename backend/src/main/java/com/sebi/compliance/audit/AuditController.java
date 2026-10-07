package com.sebi.compliance.audit;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-logs")
@Tag(name = "Audit Trail Logging", description = "Endpoints for viewing immutable system security and compliance audit logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get paginated system audit trail logs (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        PagedResponse<AuditLog> paged = auditService.getAuditLogs(page, size);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", paged));
    }
}
