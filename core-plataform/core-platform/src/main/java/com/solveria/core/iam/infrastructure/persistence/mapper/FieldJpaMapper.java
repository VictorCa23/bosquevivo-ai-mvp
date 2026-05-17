package com.solveria.core.iam.infrastructure.persistence.mapper;

import com.solveria.core.iam.domain.model.Field;
import com.solveria.core.iam.infrastructure.persistence.entity.FieldJpaEntity;
import com.solveria.core.iam.infrastructure.persistence.entity.ResourceJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between Field domain model and FieldJpaEntity.
 *
 * <p>This mapper ensures complete separation between domain and infrastructure layers.
 */
@Component
public class FieldJpaMapper {

    /**
     * Converts JPA entity to domain model.
     *
     * @param entity the JPA entity
     * @return the domain model, or null if entity is null
     */
    public Field toDomain(FieldJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Field(
                entity.getId(),
                entity.getName(),
                entity.getResource() != null ? entity.getResource().getId() : null,
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
     * <p>Note: This requires a ResourceJpaEntity to be loaded separately for the relationship.
     *
     * @param domain the domain model
     * @param resource the ResourceJpaEntity
     * @return the JPA entity, or null if domain is null
     */
    public FieldJpaEntity toEntity(Field domain, ResourceJpaEntity resource) {
        if (domain == null) {
            return null;
        }

        FieldJpaEntity entity = new FieldJpaEntity(domain.getName(), resource);
        entity.setTenantId(domain.getTenantId());
        return entity;
    }
}
