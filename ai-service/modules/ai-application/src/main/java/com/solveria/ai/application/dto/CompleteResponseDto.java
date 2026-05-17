package com.solveria.ai.application.dto;

/** Application DTO: complete response. */
public record CompleteResponseDto(String content, String model, int tokensUsed) {}
