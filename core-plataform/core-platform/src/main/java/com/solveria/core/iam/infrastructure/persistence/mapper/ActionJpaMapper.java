package com.solveria.core.iam.infrastructure.persistence.mapper;

import com.solveria.core.iam.domain.model.Action;
import com.solveria.core.iam.infrastructure.persistence.entity.ActionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between Action domain model and ActionJpaEntity.
 *
 * <p>This mapper ensures complete separation between domain and infrastructure layers.
 */
@Component
public class ActionJpaMapper {

    /**
     * Converts JPA entity to domain model.
     *
     * @param entity the JPA entity
     * @return the domain model, or null if entity is null
     */
    public Action toDomain(ActionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Action(
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
    public ActionJpaEntity toEntity(Action domain) {
        if (domain == null) {
            return null;
        }

        ActionJpaEntity entity = new ActionJpaEntity(domain.getCode(), domain.getName());
        entity.setTenantId(domain.getTenantId());
        return entity;
    }
}
