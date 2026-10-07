package com.sebi.compliance.correctiveaction;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.violation.Violation;
import com.sebi.compliance.violation.ViolationRepository;
import com.sebi.compliance.violation.ViolationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CAService {

    private final CARepository caRepository;
    private final ViolationRepository violationRepository;

    public CAService(CARepository caRepository, ViolationRepository violationRepository) {
        this.caRepository = caRepository;
        this.violationRepository = violationRepository;
    }

    public PagedResponse<CorrectiveAction> getAllActions(Long violationId, CAStatus status,
                                                        int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CorrectiveAction> paged = caRepository.findWithFilters(violationId, status, pageable);
        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public CorrectiveAction getActionById(Long id) {
        return caRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corrective Action", "id", id));
    }

    @Transactional
    public CorrectiveAction createAction(Long violationId, String description, String rootCause, String responsiblePerson, LocalDate targetDate) {
        Violation v = violationRepository.findById(violationId)
                .orElseThrow(() -> new ResourceNotFoundException("Violation", "id", violationId));

        String code = "CAPA-" + System.currentTimeMillis();
        CorrectiveAction ca = new CorrectiveAction();
        ca.setViolation(v);
        ca.setActionCode(code);
        ca.setDescription(description);
        ca.setRootCause(rootCause);
        ca.setResponsiblePerson(responsiblePerson);
        ca.setTargetDate(targetDate);
        ca.setStatus(CAStatus.OPEN);

        v.setStatus(ViolationStatus.ACTION_REQUIRED);
        violationRepository.save(v);

        return caRepository.save(ca);
    }

    @Transactional
    public CorrectiveAction verifyAction(Long id, CAStatus status, String verificationRemarks) {
        CorrectiveAction ca = getActionById(id);
        ca.setStatus(status);
        if (verificationRemarks != null) ca.setVerificationRemarks(verificationRemarks);
        if (status == CAStatus.COMPLETED) {
            ca.setCompletionDate(LocalDate.now());
            Violation v = ca.getViolation();
            v.setStatus(ViolationStatus.RESOLVED);
            v.setResolutionDate(LocalDate.now());
            v.setResolutionRemarks("Resolved via CAPA completion: " + ca.getActionCode());
            violationRepository.save(v);
        }
        return caRepository.save(ca);
    }
}
