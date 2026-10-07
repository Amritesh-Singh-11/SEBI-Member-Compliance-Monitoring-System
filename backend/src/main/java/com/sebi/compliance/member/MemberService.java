package com.sebi.compliance.member;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ApiException;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.user.User;
import com.sebi.compliance.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public MemberService(MemberRepository memberRepository, UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    public PagedResponse<MemberDTO> getAllMembers(String query, MemberStatus status, MemberType memberType,
                                                String riskLevel, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Member> members = memberRepository.findWithFilters(
                (query != null && !query.trim().isEmpty()) ? query.trim() : null,
                status,
                memberType,
                (riskLevel != null && !riskLevel.trim().isEmpty()) ? riskLevel.trim() : null,
                pageable
        );

        List<MemberDTO> content = members.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, members.getNumber(), members.getSize(), members.getTotalElements(), members.getTotalPages(), members.isLast());
    }

    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        return mapToDTO(member);
    }

    @Transactional
    public MemberDTO createMember(MemberDTO dto, String currentUsername) {
        if (memberRepository.existsByMemberCode(dto.getMemberCode())) {
            throw new ApiException("Member code '" + dto.getMemberCode() + "' already exists", HttpStatus.BAD_REQUEST);
        }
        if (memberRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new ApiException("Registration number '" + dto.getRegistrationNumber() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        Member member = new Member();
        mapToEntity(dto, member);
        member.setCreatedBy(currentUsername);
        member.setUpdatedBy(currentUsername);

        if (dto.getComplianceOfficerId() != null) {
            User officer = userRepository.findById(dto.getComplianceOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Compliance Officer", "id", dto.getComplianceOfficerId()));
            member.setComplianceOfficer(officer);
        }

        Member saved = memberRepository.save(member);
        return mapToDTO(saved);
    }

    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO dto, String currentUsername) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));

        member.setOrganizationName(dto.getOrganizationName());
        member.setRegistrationValidity(dto.getRegistrationValidity());
        member.setStatus(dto.getStatus());
        member.setAddress(dto.getAddress());
        member.setCity(dto.getCity());
        member.setState(dto.getState());
        member.setCountry(dto.getCountry());
        member.setContactEmail(dto.getContactEmail());
        member.setContactPhone(dto.getContactPhone());
        member.setUpdatedBy(currentUsername);

        if (dto.getComplianceOfficerId() != null) {
            User officer = userRepository.findById(dto.getComplianceOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Compliance Officer", "id", dto.getComplianceOfficerId()));
            member.setComplianceOfficer(officer);
        }

        Member updated = memberRepository.save(member);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        memberRepository.delete(member);
    }

    public MemberDTO mapToDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setMemberCode(member.getMemberCode());
        dto.setOrganizationName(member.getOrganizationName());
        dto.setRegistrationNumber(member.getRegistrationNumber());
        dto.setMemberType(member.getMemberType());
        dto.setRegistrationDate(member.getRegistrationDate());
        dto.setRegistrationValidity(member.getRegistrationValidity());
        dto.setStatus(member.getStatus());
        dto.setAddress(member.getAddress());
        dto.setCity(member.getCity());
        dto.setState(member.getState());
        dto.setCountry(member.getCountry());
        dto.setContactEmail(member.getContactEmail());
        dto.setContactPhone(member.getContactPhone());
        if (member.getComplianceOfficer() != null) {
            dto.setComplianceOfficerId(member.getComplianceOfficer().getId());
            dto.setComplianceOfficerName(member.getComplianceOfficer().getFullName());
        }
        dto.setRiskScore(member.getRiskScore());
        dto.setRiskLevel(member.getRiskLevel());
        return dto;
    }

    private void mapToEntity(MemberDTO dto, Member member) {
        member.setMemberCode(dto.getMemberCode());
        member.setOrganizationName(dto.getOrganizationName());
        member.setRegistrationNumber(dto.getRegistrationNumber());
        member.setMemberType(dto.getMemberType() != null ? dto.getMemberType() : MemberType.STOCK_BROKER);
        member.setRegistrationDate(dto.getRegistrationDate());
        member.setRegistrationValidity(dto.getRegistrationValidity());
        member.setStatus(dto.getStatus() != null ? dto.getStatus() : MemberStatus.ACTIVE);
        member.setAddress(dto.getAddress());
        member.setCity(dto.getCity());
        member.setState(dto.getState());
        member.setCountry(dto.getCountry() != null ? dto.getCountry() : "India");
        member.setContactEmail(dto.getContactEmail());
        member.setContactPhone(dto.getContactPhone());
    }
}
