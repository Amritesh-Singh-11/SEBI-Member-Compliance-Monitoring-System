package com.sebi.compliance.audit;

import com.sebi.compliance.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public PagedResponse<AuditLog> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> paged = auditLogRepository.findAll(pageable);
        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public void logAction(String actorUsername, String action, String entityType, Long entityId, String oldValueJson, String newValueJson, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setActorUsername(actorUsername != null ? actorUsername : "system");
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValueJson(oldValueJson);
        log.setNewValueJson(newValueJson);
        log.setIpAddress(ipAddress);

        auditLogRepository.save(log);
    }
}
