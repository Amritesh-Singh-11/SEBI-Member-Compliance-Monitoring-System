package com.sebi.compliance.requirement;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ApiException;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.member.MemberType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RequirementService {

    private final RequirementRepository requirementRepository;

    public RequirementService(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public PagedResponse<ComplianceRequirement> getAllRequirements(String query, RequirementCategory category,
                                                                   MemberType memberType, Boolean active,
                                                                   int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComplianceRequirement> paged = requirementRepository.findWithFilters(
                (query != null && !query.trim().isEmpty()) ? query.trim() : null,
                category,
                memberType,
                active,
                pageable
        );

        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public ComplianceRequirement getRequirementById(Long id) {
        return requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance Requirement", "id", id));
    }

    @Transactional
    public ComplianceRequirement createRequirement(ComplianceRequirement req) {
        if (requirementRepository.findByRequirementCode(req.getRequirementCode()).isPresent()) {
            throw new ApiException("Requirement code '" + req.getRequirementCode() + "' already exists", HttpStatus.BAD_REQUEST);
        }
        return requirementRepository.save(req);
    }

    @Transactional
    public ComplianceRequirement updateRequirement(Long id, ComplianceRequirement updateData) {
        ComplianceRequirement existing = getRequirementById(id);
        existing.setTitle(updateData.getTitle());
        existing.setDescription(updateData.getDescription());
        existing.setCategory(updateData.getCategory());
        existing.setFrequency(updateData.getFrequency());
        existing.setDueDateOffset(updateData.getDueDateOffset());
        existing.setSeverity(updateData.getSeverity());
        existing.setMandatory(updateData.getMandatory());
        existing.setApplicableMemberType(updateData.getApplicableMemberType());
        existing.setEffectiveFrom(updateData.getEffectiveFrom());
        existing.setEffectiveTo(updateData.getEffectiveTo());
        existing.setRegulatoryReference(updateData.getRegulatoryReference());
        existing.setActive(updateData.getActive());

        return requirementRepository.save(existing);
    }

    public List<ComplianceRequirement> getActiveRequirementsForMemberType(MemberType memberType) {
        return requirementRepository.findByActiveTrueAndApplicableMemberType(memberType);
    }
}
