package com.sebi.compliance.requirement;

import com.sebi.compliance.member.MemberType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequirementRepository extends JpaRepository<ComplianceRequirement, Long> {
    Optional<ComplianceRequirement> findByRequirementCode(String requirementCode);
    List<ComplianceRequirement> findByActiveTrueAndApplicableMemberType(MemberType memberType);

    @Query("SELECT r FROM ComplianceRequirement r WHERE " +
           "(:query IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.requirementCode) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR r.category = :category) AND " +
           "(:memberType IS NULL OR r.applicableMemberType = :memberType) AND " +
           "(:active IS NULL OR r.active = :active)")
    Page<ComplianceRequirement> findWithFilters(
            @Param("query") String query,
            @Param("category") RequirementCategory category,
            @Param("memberType") MemberType memberType,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
