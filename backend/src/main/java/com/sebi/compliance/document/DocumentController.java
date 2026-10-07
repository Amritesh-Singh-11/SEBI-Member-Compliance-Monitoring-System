package com.sebi.compliance.document;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/documents")
@Tag(name = "Document Management", description = "Endpoints for uploading, downloading, and reviewing compliance document evidence")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of uploaded compliance documents")
    public ResponseEntity<ApiResponse<PagedResponse<Document>>> getAllDocuments(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String documentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PagedResponse<Document> paged = documentService.getAllDocuments(memberId, status, documentType, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", paged));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document metadata by ID")
    public ResponseEntity<ApiResponse<Document>> getDocumentById(@PathVariable Long id) {
        Document doc = documentService.getDocumentById(id);
        return ResponseEntity.ok(ApiResponse.success("Document metadata retrieved successfully", doc));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload supporting compliance filing document")
    public ResponseEntity<ApiResponse<Document>> uploadDocument(
            @RequestParam Long memberId,
            @RequestParam Long complianceRecordId,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        Document doc = documentService.uploadDocument(memberId, complianceRecordId, documentType, expiryDate, file, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", doc));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download or stream document file content")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Document doc = documentService.getDocumentById(id);
        Resource resource = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFilename() + "\"")
                .body(resource);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Approve uploaded compliance document")
    public ResponseEntity<ApiResponse<Document>> approveDocument(@PathVariable Long id) {
        Document updated = documentService.reviewDocument(id, DocumentStatus.APPROVED, null);
        return ResponseEntity.ok(ApiResponse.success("Document approved successfully", updated));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Reject uploaded compliance document")
    public ResponseEntity<ApiResponse<Document>> rejectDocument(@PathVariable Long id, @RequestParam(required = false) String rejectionReason) {
        Document updated = documentService.reviewDocument(id, DocumentStatus.REJECTED, rejectionReason);
        return ResponseEntity.ok(ApiResponse.success("Document rejected", updated));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Approve or reject uploaded compliance document (Compliance Officer / Admin)")
    public ResponseEntity<ApiResponse<Document>> reviewDocument(
            @PathVariable Long id,
            @RequestParam DocumentStatus status,
            @RequestParam(required = false) String rejectionReason
    ) {
        Document updated = documentService.reviewDocument(id, status, rejectionReason);
        return ResponseEntity.ok(ApiResponse.success("Document status updated to " + status, updated));
    }
}
