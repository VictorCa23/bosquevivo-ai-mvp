package com.solveria.ai.domain.policy;

import com.solveria.ai.domain.model.Prompt;

/** Domain policy: validations / rules without framework deps. */
public interface ContentPolicy {

    boolean allows(Prompt prompt);
}
