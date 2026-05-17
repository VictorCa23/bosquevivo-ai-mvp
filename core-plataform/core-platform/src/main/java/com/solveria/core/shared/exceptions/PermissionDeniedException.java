package com.solveria.core.shared.exceptions;

import java.util.Map;

public class PermissionDeniedException extends ApplicationException {

    public PermissionDeniedException(String action) {
        super(
                "error.permission.denied",
                Map.of("action", action),
                "Permission denied for action: " + action);
    }
}
