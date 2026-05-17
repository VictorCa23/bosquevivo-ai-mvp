package com.solveria.core.iam.application.usecase;

import com.solveria.core.iam.application.command.CreateRoleCommand;
import com.solveria.core.iam.application.port.RoleRepositoryPort;
import com.solveria.core.iam.domain.model.Role;

public class CreateRoleUseCase {

    private final RoleRepositoryPort roleRepository;

    public CreateRoleUseCase(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role execute(CreateRoleCommand command) {
        Role role = new Role(command.name(), command.description());
        return roleRepository.save(role);
    }
}
