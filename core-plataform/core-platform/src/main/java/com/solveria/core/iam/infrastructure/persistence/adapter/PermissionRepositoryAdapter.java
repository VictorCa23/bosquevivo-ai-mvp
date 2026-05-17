package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.PermissionRepositoryPort;
import com.solveria.core.iam.domain.model.Permission;
import com.solveria.core.iam.infrastructure.persistence.mapper.PermissionJpaMapper;
import com.solveria.core.iam.infrastructure.persistence.repository.PermissionJpaRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;
    private final PermissionJpaMapper mapper;

    public PermissionRepositoryAdapter(
            PermissionJpaRepository permissionJpaRepository, PermissionJpaMapper mapper) {
        this.permissionJpaRepository = permissionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Set<Permission> saveAll(Set<Permission> permissions) {
        // Note: toEntity not implemented - would require loading related entities
        return Set.of();
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        permissionJpaRepository.deleteByRoleId(roleId);
    }

    @Override
    public Set<Permission> findByIds(Set<Long> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toSet());
    }
}
