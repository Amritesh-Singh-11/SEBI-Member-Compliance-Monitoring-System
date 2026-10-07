package com.sebi.compliance.violation;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberRepository;
import com.sebi.compliance.user.User;
import com.sebi.compliance.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public ViolationService(ViolationRepository violationRepository, UserRepository userRepository, MemberRepository memberRepository) {
        this.violationRepository = violationRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    public PagedResponse<Violation> getAllViolations(Long memberId, Severity severity, ViolationStatus status,
                                                     String query, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Violation> paged = violationRepository.findWithFilters(memberId, severity, status,
                (query != null && !query.trim().isEmpty()) ? query.trim() : null, pageable);
        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public Violation getViolationById(Long id) {
        return violationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Violation", "id", id));
    }

    @Transactional
    public Violation createViolation(Long memberId, String title, String description, Severity severity) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));

        Violation v = new Violation();
        v.setViolationCode("VIOL-" + member.getMemberCode() + "-" + System.currentTimeMillis());
        v.setMember(member);
        v.setTitle(title);
        v.setDescription(description);
        v.setSeverity(severity != null ? severity : Severity.MEDIUM);
        v.setStatus(ViolationStatus.OPEN);
        v.setDueDate(LocalDate.now().plusDays(15));

        return violationRepository.save(v);
    }

    @Transactional
    public Violation assignOfficer(Long violationId, Long officerUserId) {
        Violation v = getViolationById(violationId);
        User officer = userRepository.findById(officerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance Officer", "id", officerUserId));
        v.setAssignedOfficer(officer);
        v.setStatus(ViolationStatus.UNDER_REVIEW);
        return violationRepository.save(v);
    }

    @Transactional
    public Violation updateStatus(Long violationId, ViolationStatus newStatus, String resolutionRemarks) {
        Violation v = getViolationById(violationId);
        v.setStatus(newStatus);
        if (resolutionRemarks != null) {
            v.setResolutionRemarks(resolutionRemarks);
        }
        if (newStatus == ViolationStatus.RESOLVED || newStatus == ViolationStatus.CLOSED) {
            v.setResolutionDate(LocalDate.now());
        }
        return violationRepository.save(v);
    }
}
