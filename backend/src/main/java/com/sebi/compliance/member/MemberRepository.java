package com.sebi.compliance.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberCode(String memberCode);
    Optional<Member> findByRegistrationNumber(String registrationNumber);
    Boolean existsByMemberCode(String memberCode);
    Boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT m FROM Member m WHERE " +
           "(:query IS NULL OR LOWER(m.organizationName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:memberType IS NULL OR m.memberType = :memberType) AND " +
           "(:riskLevel IS NULL OR m.riskLevel = :riskLevel)")
    Page<Member> findWithFilters(
            @Param("query") String query,
            @Param("status") MemberStatus status,
            @Param("memberType") MemberType memberType,
            @Param("riskLevel") String riskLevel,
            Pageable pageable
    );

    long countByStatus(MemberStatus status);
    long countByRiskLevel(String riskLevel);
}
