package com.solveria.ai.api.response;

/** API response for POST /ai/rag/qa. */
public record RagQaResponse(String answer, int promptTokens, int completionTokens) {}
