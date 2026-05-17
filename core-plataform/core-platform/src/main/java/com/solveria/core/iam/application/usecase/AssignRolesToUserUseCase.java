package com.solveria.core.iam.application.usecase;

import com.solveria.core.iam.application.command.AssignRolesToUserCommand;
import com.solveria.core.iam.application.port.RoleRepositoryPort;
import com.solveria.core.iam.application.port.UserRepositoryPort;
import com.solveria.core.iam.domain.model.User;
import com.solveria.core.shared.exceptions.EntityNotFoundException;

public class AssignRolesToUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;

    public AssignRolesToUserUseCase(
            UserRepositoryPort userRepository, RoleRepositoryPort roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User execute(AssignRolesToUserCommand command) {

        User user =
                userRepository
                        .findById(command.userId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "User", command.userId().toString()));

        // Validate all role IDs exist
        roleRepository.findByIds(command.roleIds());

        user.assignRoleIds(command.roleIds());

        return userRepository.save(user);
    }
}
