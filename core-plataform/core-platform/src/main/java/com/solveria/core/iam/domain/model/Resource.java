package com.solveria.core.iam.domain.model;

import java.time.LocalDateTime;

/**
 * Pure domain model for Resource - no persistence framework dependencies.
 *
 * <p>Represents a resource in the system (e.g., "User", "Report", "Dashboard").
 */
public class Resource {

    private final Long id;
    private String code;
    private String name;
    private Long moduleId;
    private String tenantId;
    private Long version;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    protected Resource() {
        this.id = null;
    }

    public Resource(String code, String name, Long moduleId) {
        this.id = null;
        this.code = code;
        this.name = name;
        this.moduleId = moduleId;
    }

    public Resource(
            Long id,
            String code,
            String name,
            Long moduleId,
            String tenantId,
            Long version,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime lastModifiedAt,
            String lastModifiedBy) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.moduleId = moduleId;
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

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
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
