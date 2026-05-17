package com.solveria.ai.domain.model;

/** Domain model: AI completion result. */
public record Completion(String content, String model, int tokensUsed) {}
