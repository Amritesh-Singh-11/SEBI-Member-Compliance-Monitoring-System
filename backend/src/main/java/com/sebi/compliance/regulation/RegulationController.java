package com.sebi.compliance.regulation;

import com.sebi.compliance.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regulations")
@Tag(name = "Regulatory Library & Change Management", description = "Endpoints for managing SEBI circulars, master directions, and regulatory versions")
public class RegulationController {

    private final RegulationService regulationService;

    public RegulationController(RegulationService regulationService) {
        this.regulationService = regulationService;
    }

    @GetMapping
    @Operation(summary = "Get list of all regulatory circulars and versions")
    public ResponseEntity<ApiResponse<List<Regulation>>> getAllRegulations() {
        return ResponseEntity.ok(ApiResponse.success("Regulations retrieved successfully", regulationService.getAllRegulations()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new regulatory reference circular to library (Admin only)")
    public ResponseEntity<ApiResponse<Regulation>> createRegulation(@Valid @RequestBody Regulation reg) {
        return ResponseEntity.ok(ApiResponse.success("Regulation added successfully", regulationService.createRegulation(reg)));
    }
}
