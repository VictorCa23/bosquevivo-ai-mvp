package com.solveria.core.audit.domain.event;

import com.solveria.core.shared.events.DomainEvent;
import java.time.Instant;

/**
 * Evento de auditoría funcional.
 *
 * <p>Value Object inmutable.
 */
public record AuditEvent(String action, String entity, String entityId, Instant occurredAt)
        implements DomainEvent {}
