package com.solveria.iamservice.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;

public record AssignPermissionsToRoleRequest(
        @NotNull(message = "{validation.role.id.required}") Long roleId,
        @NotEmpty(message = "{validation.permission.ids.required}")
                Collection<@NotNull Long> permissionIds) {}
