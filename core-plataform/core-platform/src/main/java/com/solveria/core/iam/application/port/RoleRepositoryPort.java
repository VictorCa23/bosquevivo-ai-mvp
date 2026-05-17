package com.solveria.core.iam.application.port;

import com.solveria.core.iam.domain.model.Role;
import java.util.Optional;
import java.util.Set;

public interface RoleRepositoryPort {

    Role save(Role role);

    Optional<Role> findById(Long id);

    Set<Role> findByIds(Set<Long> ids);
}
