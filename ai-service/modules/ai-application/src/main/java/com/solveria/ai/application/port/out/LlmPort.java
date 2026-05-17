package com.solveria.ai.application.port.out;

import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;

/** Port out: LLM / completion provider. */
public interface LlmPort {

    Completion complete(Prompt prompt);
}
