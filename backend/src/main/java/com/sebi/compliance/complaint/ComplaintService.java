package com.sebi.compliance.complaint;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.member.Member;
import com.sebi.compliance.member.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final MemberRepository memberRepository;

    public ComplaintService(ComplaintRepository complaintRepository, MemberRepository memberRepository) {
        this.complaintRepository = complaintRepository;
        this.memberRepository = memberRepository;
    }

    public PagedResponse<Complaint> getAllComplaints(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Complaint> paged = (memberId != null) ?
                complaintRepository.findByMemberId(memberId, pageable) : complaintRepository.findAll(pageable);
        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public Complaint createComplaint(Long memberId, String category, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));

        Complaint c = new Complaint();
        c.setComplaintCode("CMPL-" + System.currentTimeMillis());
        c.setMember(member);
        c.setCategory(category);
        c.setReceivedDate(LocalDate.now());
        c.setDescription(description);
        c.setStatus("OPEN");

        return complaintRepository.save(c);
    }
}
