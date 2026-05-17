package com.solveria.core.shared.exceptions;

import java.util.Map;

public abstract class DomainException extends SolverException {

    protected DomainException(String code, Map<String, Object> args, String technicalMessage) {
        super(code, args, technicalMessage);
    }
}
