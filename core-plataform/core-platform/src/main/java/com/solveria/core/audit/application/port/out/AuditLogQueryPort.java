package com.solveria.core.audit.application.port.out;

import com.solveria.core.audit.domain.model.AuditLog;
import java.time.Instant;
import java.util.List;

/**
 * Port (outbound) para consultas de auditoría. Application no depende de infraestructura; infra
 * implementa este port.
 */
public interface AuditLogQueryPort {

    List<AuditLog> findByTenantId(String tenantId);

    List<AuditLog> findByOccurredAtBetween(Instant from, Instant to);
}
