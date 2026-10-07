package com.sebi.compliance.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByComplianceRecordId(Long complianceRecordId);
    List<Document> findByMemberId(Long memberId);
    Optional<Document> findByStorageKey(String storageKey);

    @Query("SELECT d FROM Document d WHERE " +
           "(:memberId IS NULL OR d.member.id = :memberId) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:documentType IS NULL OR d.documentType = :documentType)")
    Page<Document> findWithFilters(
            @Param("memberId") Long memberId,
            @Param("status") DocumentStatus status,
            @Param("documentType") String documentType,
            Pageable pageable
    );
}
