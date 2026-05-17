package com.solveria.core.iam.application.usecase;

import com.solveria.core.iam.application.command.AssignPermissionsToRoleCommand;
import com.solveria.core.iam.application.port.PermissionRepositoryPort;
import com.solveria.core.iam.application.port.RoleRepositoryPort;
import com.solveria.core.iam.domain.model.Permission;
import com.solveria.core.iam.domain.model.Role;
import com.solveria.core.shared.exceptions.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;

public class AssignPermissionsToRoleUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;

    public AssignPermissionsToRoleUseCase(
            RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role execute(AssignPermissionsToRoleCommand command) {

        Role role =
                roleRepository
                        .findById(command.roleId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Role", command.roleId().toString()));

        if (command.permissionIds() == null || command.permissionIds().isEmpty()) {
            return roleRepository.save(role);
        }

        Set<Long> permissionIds = Set.copyOf(command.permissionIds());

        // Validate all permission IDs exist
        Set<Permission> permissions = permissionRepository.findByIds(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            Set<Long> foundIds =
                    permissions.stream().map(Permission::getId).collect(Collectors.toSet());
            Long missingId =
                    permissionIds.stream()
                            .filter(id -> !foundIds.contains(id))
                            .findFirst()
                            .orElseThrow();
            throw new EntityNotFoundException("Permission", missingId.toString());
        }

        role.assignPermissionIds(permissionIds);
        return roleRepository.save(role);
    }
}
