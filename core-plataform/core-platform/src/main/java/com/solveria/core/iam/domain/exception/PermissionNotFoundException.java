package com.solveria.core.iam.domain.exception;

import com.solveria.core.shared.exceptions.ApplicationException;
import java.util.Map;
import java.util.Set;

public class PermissionNotFoundException extends ApplicationException {

    public PermissionNotFoundException(Set<Long> permissionIds) {
        super(
                "IAM_PERMISSION_NOT_FOUND",
                Map.of("permissionIds", permissionIds),
                "Permissions not found: " + permissionIds);
    }
}
