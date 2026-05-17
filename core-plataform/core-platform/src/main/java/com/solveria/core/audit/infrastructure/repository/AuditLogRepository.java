package com.solveria.core.audit.infrastructure.repository;

import com.solveria.core.audit.domain.model.AuditLog;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de auditoría funcional.
 *
 * <p>Infraestructura pura (JPA).
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTenantId(String tenantId);

    List<AuditLog> findByOccurredAtBetween(Instant from, Instant to);
}
