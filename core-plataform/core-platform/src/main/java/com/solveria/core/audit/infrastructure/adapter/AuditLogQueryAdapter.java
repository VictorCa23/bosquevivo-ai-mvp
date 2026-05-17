package com.solveria.core.audit.infrastructure.adapter;

import com.solveria.core.audit.application.port.out.AuditLogQueryPort;
import com.solveria.core.audit.domain.model.AuditLog;
import com.solveria.core.audit.infrastructure.repository.AuditLogRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** Adapter que implementa AuditLogQueryPort delegando al repositorio JPA. */
@Component
public class AuditLogQueryAdapter implements AuditLogQueryPort {

    private final AuditLogRepository repository;

    public AuditLogQueryAdapter(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AuditLog> findByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    public List<AuditLog> findByOccurredAtBetween(Instant from, Instant to) {
        return repository.findByOccurredAtBetween(from, to);
    }
}
