package com.solveria.core.iam.domain.model;

import java.time.LocalDateTime;

/**
 * Pure domain model for Field - no persistence framework dependencies.
 *
 * <p>Represents a field within a resource (e.g., "email" field in "User" resource).
 */
public class Field {

    private final Long id;
    private String name;
    private Long resourceId;
    private String tenantId;
    private Long version;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    protected Field() {
        this.id = null;
    }

    public Field(String name, Long resourceId) {
        this.id = null;
        this.name = name;
        this.resourceId = resourceId;
    }

    public Field(
            Long id,
            String name,
            Long resourceId,
            String tenantId,
            Long version,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime lastModifiedAt,
            String lastModifiedBy) {
        this.id = id;
        this.name = name;
        this.resourceId = resourceId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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
