package com.solveria.core.iam.domain.model;

import java.time.LocalDateTime;

/**
 * Pure domain model for Action - no persistence framework dependencies.
 *
 * <p>Represents an action that can be performed on resources (CREATE, READ, UPDATE, DELETE,
 * EXECUTE).
 */
public class Action {

    private final Long id;
    private String code;
    private String name;
    private String tenantId;
    private Long version;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    protected Action() {
        this.id = null;
    }

    public Action(String code, String name) {
        this.id = null;
        this.code = code;
        this.name = name;
    }

    public Action(
            Long id,
            String code,
            String name,
            String tenantId,
            Long version,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime lastModifiedAt,
            String lastModifiedBy) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.tenantId = tenantId;
        this.version = version;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
}
