package com.solveria.core.audit.domain.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    private String entity;

    private String entityId;

    /** Actor semántico (NO Long) */
    private String userId;

    private String tenantId;

    private Instant occurredAt;

    protected AuditLog() {}

    public AuditLog(
            String action,
            String entity,
            String entityId,
            String userId,
            String tenantId,
            Instant occurredAt) {
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.occurredAt = occurredAt;
    }
}
