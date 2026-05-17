package com.solveria.core.shared.exceptions;

import java.util.Collections;

public class SolverExceptionImpl extends SolverException {

    public SolverExceptionImpl(String code) {
        super(code, Collections.emptyMap(), null);
    }
}
