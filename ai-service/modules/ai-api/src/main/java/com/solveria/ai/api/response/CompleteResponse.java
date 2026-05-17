package com.solveria.ai.api.response;

/** API response DTO for /complete. */
public record CompleteResponse(String content, String model, int tokensUsed) {}
