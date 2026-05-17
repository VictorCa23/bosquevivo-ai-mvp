package com.solveria.core.iam.domain.model;

import java.time.LocalDateTime;

/**
 * Pure domain model for Permission - no persistence framework dependencies.
 *
 * <p>Represents a permission granted to a role for accessing a specific resource with an action.
 */
public class Permission {

    private final Long id;
    private Long roleId;
    private Long moduleId;
    private Long resourceId;
    private Long actionId;
    private Long fieldId;
    private String tenantId;
    private Long version;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    protected Permission() {
        this.id = null;
    }

    public Permission(Long roleId, Long moduleId, Long resourceId, Long actionId, Long fieldId) {
        this.id = null;
        this.roleId = roleId;
        this.moduleId = moduleId;
        this.resourceId = resourceId;
        this.actionId = actionId;
        this.fieldId = fieldId;
    }

    public Permission(
            Long id,
            Long roleId,
            Long moduleId,
            Long resourceId,
            Long actionId,
            Long fieldId,
            String tenantId,
            Long version,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime lastModifiedAt,
            String lastModifiedBy) {
        this.id = id;
        this.roleId = roleId;
        this.moduleId = moduleId;
        this.resourceId = resourceId;
        this.actionId = actionId;
        this.fieldId = fieldId;
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

    public Long getRoleId() {
        return roleId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public Long getActionId() {
        return actionId;
    }

    public Long getFieldId() {
        return fieldId;
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
