package com.solveria.ai.application.port.in;

import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;

/** Port in: complete prompt use case. */
public interface CompletePromptUseCase {

    Completion complete(Prompt prompt);
}
