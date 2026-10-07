package com.sebi.compliance.member;

import com.sebi.compliance.common.dto.ApiResponse;
import com.sebi.compliance.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@Tag(name = "Member Management", description = "Endpoints for managing Regulated Market Intermediaries (Stock Brokers)")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of regulated members with search and risk filters")
    public ResponseEntity<ApiResponse<PagedResponse<MemberDTO>>> getAllMembers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) MemberType memberType,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "organizationName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PagedResponse<MemberDTO> response = memberService.getAllMembers(query, status, memberType, riskLevel, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Members retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comprehensive member details by ID")
    public ResponseEntity<ApiResponse<MemberDTO>> getMemberById(@PathVariable Long id) {
        MemberDTO dto = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.success("Member profile retrieved successfully", dto));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new regulated member entity (Admin only)")
    public ResponseEntity<ApiResponse<MemberDTO>> createMember(@Valid @RequestBody MemberDTO dto, Authentication authentication) {
        MemberDTO created = memberService.createMember(dto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Member registered successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")
    @Operation(summary = "Update member registration status and details")
    public ResponseEntity<ApiResponse<MemberDTO>> updateMember(@PathVariable Long id, @Valid @RequestBody MemberDTO dto, Authentication authentication) {
        MemberDTO updated = memberService.updateMember(id, dto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Member profile updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate/Remove regulated member entity (Admin only)")
    public ResponseEntity<ApiResponse<String>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", "Member ID " + id + " removed"));
    }
}
