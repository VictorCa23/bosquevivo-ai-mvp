package com.solveria.core.iam.infrastructure.persistence.mapper;

import com.solveria.core.iam.domain.model.User;
import com.solveria.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import com.solveria.core.iam.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Maps between User domain model and UserJpaEntity.
 *
 * <p>This mapper ensures complete separation between domain and infrastructure layers.
 */
@Component
public class UserJpaMapper {

    /**
     * Converts JPA entity to domain model.
     *
     * @param entity the JPA entity
     * @return the domain model, or null if entity is null
     */
    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Set<Long> roleIds =
                entity.getRoles() != null
                        ? entity.getRoles().stream()
                                .map(RoleJpaEntity::getId)
                                .collect(Collectors.toSet())
                        : Set.of();

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.isActive(),
                roleIds,
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
     * <p>Note: This requires RoleJpaEntity entities to be loaded separately for the relationship.
     *
     * @param domain the domain model
     * @param roles the Set of RoleJpaEntity entities
     * @return the JPA entity, or null if domain is null
     */
    public UserJpaEntity toEntity(User domain, Set<RoleJpaEntity> roles) {
        if (domain == null) {
            return null;
        }

        UserJpaEntity entity =
                new UserJpaEntity(domain.getUsername(), domain.getEmail(), domain.isActive());
        entity.setTenantId(domain.getTenantId());
        entity.assignRoles(roles);
        return entity;
    }
}
