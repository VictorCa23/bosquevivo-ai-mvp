package com.solveria.core.iam.infrastructure.persistence.mapper;

import com.solveria.core.iam.domain.model.Resource;
import com.solveria.core.iam.infrastructure.persistence.entity.ModuleJpaEntity;
import com.solveria.core.iam.infrastructure.persistence.entity.ResourceJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between Resource domain model and ResourceJpaEntity.
 *
 * <p>This mapper ensures complete separation between domain and infrastructure layers.
 */
@Component
public class ResourceJpaMapper {

    /**
     * Converts JPA entity to domain model.
     *
     * @param entity the JPA entity
     * @return the domain model, or null if entity is null
     */
    public Resource toDomain(ResourceJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Resource(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getModule() != null ? entity.getModule().getId() : null,
                entity.getTenantId(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getLastModifiedAt(),
                entity.getLastModifiedBy());
    }

    /**
     * Converts domain model to JPA entity.
     *
     * <p>Note: This requires a ModuleJpaEntity to be loaded separately for the relationship.
     *
     * @param domain the domain model
     * @param module the ModuleJpaEntity
     * @return the JPA entity, or null if domain is null
     */
    public ResourceJpaEntity toEntity(Resource domain, ModuleJpaEntity module) {
        if (domain == null) {
            return null;
        }

        ResourceJpaEntity entity =
                new ResourceJpaEntity(domain.getCode(), domain.getName(), module);
        entity.setTenantId(domain.getTenantId());
        return entity;
    }
}
