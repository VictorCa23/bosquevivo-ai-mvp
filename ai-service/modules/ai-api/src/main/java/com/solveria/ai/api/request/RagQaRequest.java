package com.solveria.ai.api.request;

import jakarta.validation.constraints.NotBlank;

/** API request for POST /ai/rag/qa. */
public record RagQaRequest(
        @NotBlank(message = "question must not be blank") String question,
        @NotBlank(message = "namespace must not be blank") String namespace) {}
