package com.sebi.compliance.complaint;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/complaints")
@Tag(name = "Compliance Complaints Tracker", description = "Endpoints for internal compliance grievance tracking")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of internal compliance complaints")
    public ResponseEntity<ApiResponse<PagedResponse<Complaint>>> getAllComplaints(
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<Complaint> paged = complaintService.getAllComplaints(memberId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Complaints retrieved successfully", paged));
    }

    @PostMapping
    @Operation(summary = "Log new compliance complaint ticket")
    public ResponseEntity<ApiResponse<Complaint>> createComplaint(
            @RequestParam Long memberId,
            @RequestParam String category,
            @RequestParam String description
    ) {
        Complaint created = complaintService.createComplaint(memberId, category, description);
        return ResponseEntity.ok(ApiResponse.success("Complaint logged successfully", created));
    }
}
