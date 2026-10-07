package com.sebi.compliance.document;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ApiException;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberRepository;
import com.sebi.compliance.record.ComplianceRecord;
import com.sebi.compliance.record.RecordRepository;
import com.sebi.compliance.record.RecordStatus;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private final StorageService storageService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "docx", "xlsx", "csv", "png", "jpg", "jpeg");
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25 MB

    public DocumentService(DocumentRepository documentRepository, RecordRepository recordRepository,
                           MemberRepository memberRepository, StorageService storageService) {
        this.documentRepository = documentRepository;
        this.recordRepository = recordRepository;
        this.memberRepository = memberRepository;
        this.storageService = storageService;
    }

    public PagedResponse<Document> getAllDocuments(Long memberId, DocumentStatus status, String documentType,
                                                  int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> paged = documentRepository.findWithFilters(memberId, status, documentType, pageable);
        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
    }

    @Transactional
    public Document uploadDocument(Long memberId, Long complianceRecordId, String documentType,
                                   LocalDate expiryDate, MultipartFile file, String uploadedByUsername) {
        validateFile(file);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));
        ComplianceRecord record = recordRepository.findById(complianceRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance Record", "id", complianceRecordId));

        String checksum;
        try {
            checksum = storageService.calculateChecksum(file.getInputStream());
        } catch (IOException e) {
            checksum = "unknown";
        }

        String subDir = "member_" + memberId;
        String storageKey = storageService.store(file, subDir);

        Document doc = new Document();
        doc.setMember(member);
        doc.setComplianceRecord(record);
        doc.setDocumentType(documentType != null ? documentType : record.getRequirement().getCategory().name());
        doc.setOriginalFilename(file.getOriginalFilename());
        doc.setStorageKey(storageKey);
        doc.setContentType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setChecksum(checksum);
        doc.setUploadedBy(uploadedByUsername);
        doc.setExpiryDate(expiryDate);
        doc.setStatus(DocumentStatus.UPLOADED);

        Document savedDoc = documentRepository.save(doc);

        // Update compliance record status to SUBMITTED
        record.setStatus(RecordStatus.SUBMITTED);
        record.setSubmissionDate(LocalDate.now());
        recordRepository.save(record);

        return savedDoc;
    }

    @Transactional
    public Document reviewDocument(Long documentId, DocumentStatus status, String rejectionReason) {
        Document doc = getDocumentById(documentId);
        doc.setStatus(status);
        if (status == DocumentStatus.REJECTED) {
            doc.setRejectionReason(rejectionReason);
            doc.getComplianceRecord().setStatus(RecordStatus.REJECTED);
        } else if (status == DocumentStatus.APPROVED) {
            doc.getComplianceRecord().setStatus(RecordStatus.COMPLIANT);
        }
        recordRepository.save(doc.getComplianceRecord());
        return documentRepository.save(doc);
    }

    public Resource downloadDocument(Long documentId) {
        Document doc = getDocumentById(documentId);
        return storageService.loadAsResource(doc.getStorageKey());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException("Uploaded file is empty", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException("File size exceeds 25MB threshold limit", HttpStatus.BAD_REQUEST);
        }
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ApiException("Invalid filename", HttpStatus.BAD_REQUEST);
        }
        int extIndex = filename.lastIndexOf('.');
        if (extIndex == -1) {
            throw new ApiException("File must have a valid extension", HttpStatus.BAD_REQUEST);
        }
        String ext = filename.substring(extIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new ApiException("Unsupported file format: '" + ext + "'. Allowed formats: PDF, DOCX, XLSX, CSV, PNG, JPG", HttpStatus.BAD_REQUEST);
        }
    }
}
