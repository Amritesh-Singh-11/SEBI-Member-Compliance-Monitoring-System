package com.sebi.compliance.record;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ApiException;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberRepository;
import com.sebi.compliance.requirement.ComplianceRequirement;
import com.sebi.compliance.requirement.RequirementRepository;
import com.sebi.compliance.user.User;
import com.sebi.compliance.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private final RequirementRepository requirementRepository;
    private final UserRepository userRepository;

    public RecordService(RecordRepository recordRepository, MemberRepository memberRepository,
                         RequirementRepository requirementRepository, UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.memberRepository = memberRepository;
        this.requirementRepository = requirementRepository;
        this.userRepository = userRepository;
    }

    public PagedResponse<ComplianceRecord> getAllRecords(Long memberId, Long requirementId, RecordStatus status,
                                                        int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComplianceRecord> paged = recordRepository.findWithFilters(memberId, requirementId, status, pageable);
        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public ComplianceRecord getRecordById(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance Record", "id", id));
    }

    @Transactional
    public ComplianceRecord createRecord(Long memberId, Long requirementId, LocalDate periodStart, LocalDate periodEnd, LocalDate dueDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));
        ComplianceRequirement req = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance Requirement", "id", requirementId));

        if (recordRepository.findByMemberIdAndRequirementIdAndPeriodStartAndPeriodEnd(memberId, requirementId, periodStart, periodEnd).isPresent()) {
            throw new ApiException("A compliance record already exists for this member, requirement and period.", HttpStatus.BAD_REQUEST);
        }

        ComplianceRecord record = new ComplianceRecord();
        record.setMember(member);
        record.setRequirement(req);
        record.setPeriodStart(periodStart);
        record.setPeriodEnd(periodEnd);
        record.setDueDate(dueDate != null ? dueDate : periodEnd.plusDays(req.getDueDateOffset() != null ? req.getDueDateOffset() : 15));
        record.setStatus(RecordStatus.PENDING);

        return recordRepository.save(record);
    }

    @Transactional
    public ComplianceRecord updateStatus(Long recordId, RecordStatus newStatus, String remarks, Long reviewerUserId) {
        ComplianceRecord record = getRecordById(recordId);
        record.setStatus(newStatus);
        if (remarks != null) record.setRemarks(remarks);

        if (reviewerUserId != null) {
            User reviewer = userRepository.findById(reviewerUserId).orElse(null);
            record.setReviewedBy(reviewer);
            record.setReviewedAt(LocalDateTime.now());
        }

        if (newStatus == RecordStatus.SUBMITTED && record.getSubmissionDate() == null) {
            record.setSubmissionDate(LocalDate.now());
        }

        return recordRepository.save(record);
    }

    public List<ComplianceRecord> getMemberRecords(Long memberId) {
        return recordRepository.findByMemberId(memberId);
    }
}
