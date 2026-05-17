package com.solveria.ai.application.service;

import com.solveria.ai.application.port.in.CompletePromptUseCase;
import com.solveria.ai.application.port.out.LlmPort;
import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;

/** Use case: complete prompt via LLM port. */
public class CompletePromptService implements CompletePromptUseCase {

    private final LlmPort llmPort;

    public CompletePromptService(LlmPort llmPort) {
        this.llmPort = llmPort;
    }

    @Override
    public Completion complete(Prompt prompt) {
        return llmPort.complete(prompt);
    }
}
