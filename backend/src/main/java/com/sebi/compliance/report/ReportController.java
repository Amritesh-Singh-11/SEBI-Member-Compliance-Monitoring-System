package com.sebi.compliance.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@Tag(name = "Compliance & Risk Reporting Engine", description = "Endpoints for generating downloadable PDF and CSV regulatory compliance reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/compliance/pdf")
    @Operation(summary = "Generate and download master compliance & risk summary PDF report")
    public ResponseEntity<byte[]> downloadCompliancePdfReport() {
        byte[] pdfBytes = reportService.generateCompliancePdfReport();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sebi_compliance_master_report.pdf\"")
                .body(pdfBytes);
    }

    @GetMapping("/compliance/csv")
    @Operation(summary = "Generate and download member compliance CSV report")
    public ResponseEntity<String> downloadComplianceCsvReport() {
        String csvData = reportService.generateComplianceCsvReport();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sebi_members_summary.csv\"")
                .body(csvData);
    }
}
