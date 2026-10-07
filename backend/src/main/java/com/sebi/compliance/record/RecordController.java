package com.sebi.compliance.record;

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
@RequestMapping({"/compliance", "/compliance/records"})
@Tag(name = "Compliance Records Tracking", description = "Endpoints for tracking member regulatory period filings and deadlines")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of member compliance period records with filters")
    public ResponseEntity<ApiResponse<PagedResponse<ComplianceRecord>>> getAllRecords(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long requirementId,
            @RequestParam(required = false) RecordStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PagedResponse<ComplianceRecord> paged = recordService.getAllRecords(memberId, requirementId, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Compliance records retrieved successfully", paged));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single compliance record by ID")
    public ResponseEntity<ApiResponse<ComplianceRecord>> getRecordById(@PathVariable Long id) {
        ComplianceRecord record = recordService.getRecordById(id);
        return ResponseEntity.ok(ApiResponse.success("Compliance record retrieved successfully", record));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Assign a compliance requirement to a member for a filing period")
    public ResponseEntity<ApiResponse<ComplianceRecord>> createRecord(
            @RequestParam Long memberId,
            @RequestParam Long requirementId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        ComplianceRecord created = recordService.createRecord(memberId, requirementId, periodStart, periodEnd, dueDate);
        return ResponseEntity.ok(ApiResponse.success("Compliance record created and assigned successfully", created));
    }

    @PutMapping("/{id}")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Update status of a compliance record (Review / Approve / Reject)")
    public ResponseEntity<ApiResponse<ComplianceRecord>> updateStatus(
            @PathVariable Long id,
            @RequestParam RecordStatus status,
            @RequestParam(required = false) String remarks
    ) {
        ComplianceRecord updated = recordService.updateStatus(id, status, remarks, null);
        return ResponseEntity.ok(ApiResponse.success("Record status updated to " + status, updated));
    }
}
