package com.solveria.core.iam.application.command;

import java.util.Set;

public record AssignRolesToUserCommand(Long userId, Set<Long> roleIds) {}
