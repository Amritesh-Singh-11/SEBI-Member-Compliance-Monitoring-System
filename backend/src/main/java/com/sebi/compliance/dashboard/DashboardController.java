package com.sebi.compliance.dashboard;

import com.sebi.compliance.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Analytics & Dashboard", description = "Endpoints for real-time compliance metrics, risk distributions, and executive summaries")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/metrics", "/summary"})
    @Operation(summary = "Get real-time aggregated dashboard metrics and chart distributions from database")
    public ResponseEntity<ApiResponse<DashboardMetricsDTO>> getMetrics() {
        DashboardMetricsDTO metrics = dashboardService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics calculated successfully", metrics));
    }

    @GetMapping("/compliance")
    @Operation(summary = "Get compliance status distribution for dashboard")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getComplianceDistribution() {
        DashboardMetricsDTO metrics = dashboardService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success("Compliance distribution retrieved", metrics.getComplianceStatusDistribution()));
    }

    @GetMapping("/violations")
    @Operation(summary = "Get violation distribution for dashboard")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getViolationDistribution() {
        DashboardMetricsDTO metrics = dashboardService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success("Violation distribution retrieved", metrics.getViolationsBySeverity()));
    }

    @GetMapping("/risk")
    @Operation(summary = "Get risk distribution for dashboard")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getRiskDistribution() {
        DashboardMetricsDTO metrics = dashboardService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success("Risk distribution retrieved", metrics.getRiskLevelDistribution()));
    }
}
