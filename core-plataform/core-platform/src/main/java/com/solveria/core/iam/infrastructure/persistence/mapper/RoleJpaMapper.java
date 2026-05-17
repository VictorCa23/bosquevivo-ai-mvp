package com.solveria.core.iam.infrastructure.persistence.mapper;

import com.solveria.core.iam.domain.model.Role;
import com.solveria.core.iam.infrastructure.persistence.entity.PermissionJpaEntity;
import com.solveria.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Maps between Role domain model and RoleJpaEntity.
 *
 * <p>This mapper ensures complete separation between domain and infrastructure layers.
 */
@Component
public class RoleJpaMapper {

    /**
     * Converts JPA entity to domain model.
     *
     * @param entity the JPA entity
     * @return the domain model, or null if entity is null
     */
    public Role toDomain(RoleJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Set<Long> permissionIds =
                entity.getPermissions() != null
                        ? entity.getPermissions().stream()
                                .map(PermissionJpaEntity::getId)
                                .collect(Collectors.toSet())
                        : Set.of();

        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                permissionIds,
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
    public RoleJpaEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }

        RoleJpaEntity entity = new RoleJpaEntity(domain.getName(), domain.getDescription());
        entity.setTenantId(domain.getTenantId());
        return entity;
    }
}
