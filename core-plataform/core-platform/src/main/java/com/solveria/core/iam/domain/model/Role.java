package com.solveria.core.iam.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Pure domain model for Role - no persistence framework dependencies.
 *
 * <p>Represents a role with associated permissions.
 */
public class Role {

    private final Long id;
    private String name;
    private String description;
    private Set<Long> permissionIds = new HashSet<>();
    private String tenantId;
    private Long version;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    protected Role() {
        this.id = null;
    }

    public Role(String name, String description) {
        this.id = null;
        this.name = name;
        this.description = description;
    }

    public Role(
            Long id,
            String name,
            String description,
            Set<Long> permissionIds,
            String tenantId,
            Long version,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime lastModifiedAt,
            String lastModifiedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds != null ? new HashSet<>(permissionIds) : new HashSet<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getPermissionIds() {
        return new HashSet<>(permissionIds);
    }

    public void assignPermissionIds(Set<Long> permissionIds) {
        this.permissionIds.clear();
        if (permissionIds != null) {
            this.permissionIds.addAll(permissionIds);
        }
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
