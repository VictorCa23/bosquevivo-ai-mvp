package com.solveria.ai.application.dto;

/** DTO: RAG QA use case output (answer + usage). */
public record RagQaResultDto(String answer, int promptTokens, int completionTokens) {

    public int totalTokens() {
        return promptTokens + completionTokens;
    }
}
