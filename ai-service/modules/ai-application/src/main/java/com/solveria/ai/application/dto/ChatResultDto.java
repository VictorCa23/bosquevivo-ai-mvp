package com.solveria.ai.application.dto;

/** Generic DTO: LLM chat result (answer + token usage). Used by LlmChatPort. */
public record ChatResultDto(String answer, int promptTokens, int completionTokens) {

    public int totalTokens() {
        return promptTokens + completionTokens;
    }
}
