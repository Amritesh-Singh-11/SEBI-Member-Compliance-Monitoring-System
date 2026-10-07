package com.sebi.compliance.report;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberRepository;
import com.sebi.compliance.record.ComplianceRecord;
import com.sebi.compliance.record.RecordRepository;
import com.sebi.compliance.violation.Violation;
import com.sebi.compliance.violation.ViolationRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;
    private final ViolationRepository violationRepository;

    public ReportService(MemberRepository memberRepository, RecordRepository recordRepository,
                         ViolationRepository violationRepository) {
        this.memberRepository = memberRepository;
        this.recordRepository = recordRepository;
        this.violationRepository = violationRepository;
    }

    public byte[] generateCompliancePdfReport() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Header Title
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("SEBI MEMBER COMPLIANCE MONITORING SYSTEM", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, Color.GRAY);
            Paragraph subtitle = new Paragraph("An Academic RegTech Prototype - Master Compliance & Risk Report", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // Timestamp & Disclaimer Note
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.LIGHT_GRAY);
            Paragraph meta = new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | Academic Prototype", smallFont);
            meta.setSpacingAfter(20);
            document.add(meta);

            // Members Table Section
            Paragraph sec1 = new Paragraph("1. Regulated Member Overview", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
            sec1.setSpacingAfter(10);
            document.add(sec1);

            PdfPTable memberTable = new PdfPTable(5);
            memberTable.setWidthPercentage(100);
            memberTable.setWidths(new float[]{2f, 3f, 2f, 1.5f, 1.5f});

            addTableHeader(memberTable, new String[]{"Code", "Organization Name", "Reg No.", "Risk Score", "Risk Level"});

            List<Member> members = memberRepository.findAll();
            for (Member m : members) {
                memberTable.addCell(createCell(m.getMemberCode()));
                memberTable.addCell(createCell(m.getOrganizationName()));
                memberTable.addCell(createCell(m.getRegistrationNumber()));
                memberTable.addCell(createCell(String.valueOf(m.getRiskScore())));
                memberTable.addCell(createCell(m.getRiskLevel()));
            }
            document.add(memberTable);

            // Active Violations Section
            Paragraph sec2 = new Paragraph("\n2. Detected Compliance Violations", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
            sec2.setSpacingAfter(10);
            document.add(sec2);

            PdfPTable violTable = new PdfPTable(4);
            violTable.setWidthPercentage(100);
            violTable.setWidths(new float[]{2f, 2.5f, 4f, 1.5f});

            addTableHeader(violTable, new String[]{"Code", "Member Code", "Title", "Severity"});

            List<Violation> violations = violationRepository.findAll();
            for (Violation v : violations) {
                violTable.addCell(createCell(v.getViolationCode()));
                violTable.addCell(createCell(v.getMember().getMemberCode()));
                violTable.addCell(createCell(v.getTitle()));
                violTable.addCell(createCell(v.getSeverity().name()));
            }
            document.add(violTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public String generateComplianceCsvReport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Member Code,Organization Name,Registration Number,Status,Risk Score,Risk Level\n");
        List<Member> members = memberRepository.findAll();
        for (Member m : members) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%.1f,\"%s\"\n",
                    m.getMemberCode(), m.getOrganizationName(), m.getRegistrationNumber(),
                    m.getStatus(), m.getRiskScore(), m.getRiskLevel()));
        }
        return csv.toString();
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(new Color(15, 23, 42)); // Dark Navy
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private PdfPCell createCell(String value) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "-", font));
        cell.setPadding(5);
        return cell;
    }
}
