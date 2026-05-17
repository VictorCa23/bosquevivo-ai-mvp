package com.solveria.core.iam.infrastructure.persistence.repository;

import com.solveria.core.iam.infrastructure.persistence.entity.PermissionJpaEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionJpaRepository
        extends JpaRepository<PermissionJpaEntity, Long>,
                JpaSpecificationExecutor<PermissionJpaEntity> {
    void deleteByRoleId(Long roleId);

    Set<PermissionJpaEntity> findByRoleId(Long roleId);
}
