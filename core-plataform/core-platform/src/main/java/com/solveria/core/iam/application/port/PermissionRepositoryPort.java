package com.solveria.core.iam.application.port;

import com.solveria.core.iam.domain.model.Permission;
import java.util.Set;

public interface PermissionRepositoryPort {

    Set<Permission> saveAll(Set<Permission> permissions);

    void deleteByRoleId(Long roleId);

    Set<Permission> findByIds(Set<Long> ids);
}
