package com.solveria.core.iam.infrastructure.persistence.mapper;

import com.solveria.core.iam.domain.model.Module;
import com.solveria.core.iam.infrastructure.persistence.entity.ModuleJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between Module domain model and ModuleJpaEntity.
 *
 * <p>This mapper ensures complete separation between domain and infrastructure layers.
 */
@Component
public class ModuleJpaMapper {

    /**
     * Converts JPA entity to domain model.
     *
     * @param entity the JPA entity
     * @return the domain model, or null if entity is null
     */
    public Module toDomain(ModuleJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Module(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
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
     * @param domain the domain model
     * @return the JPA entity, or null if domain is null
     */
    public ModuleJpaEntity toEntity(Module domain) {
        if (domain == null) {
            return null;
        }

        ModuleJpaEntity entity = new ModuleJpaEntity(domain.getCode(), domain.getName());
        entity.setTenantId(domain.getTenantId());
        return entity;
    }
}
