package com.solveria.core.shared.exceptions;

import java.util.Collections;
import java.util.Map;

public abstract class SolverException extends RuntimeException {

    private final String code;
    private final Map<String, Object> args;

    protected SolverException(String code) {
        this(code, Collections.emptyMap(), null);
    }

    protected SolverException(String code, Map<String, Object> args, String technicalMessage) {
        super(technicalMessage); // SOLO para logs / developers
        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getArgs() {
        return args;
    }
}
