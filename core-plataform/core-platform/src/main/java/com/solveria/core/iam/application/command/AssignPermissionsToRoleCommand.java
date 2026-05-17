package com.solveria.core.iam.application.command;

import java.util.Collection;

public record AssignPermissionsToRoleCommand(Long roleId, Collection<Long> permissionIds) {}
