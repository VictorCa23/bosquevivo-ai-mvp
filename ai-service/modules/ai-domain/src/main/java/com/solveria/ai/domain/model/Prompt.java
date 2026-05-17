package com.solveria.ai.domain.model;

import java.util.Map;

/** Domain model: user prompt for AI completion. */
public record Prompt(String text, Map<String, Object> metadata) {

    public Prompt {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
    }

    public static Prompt of(String text) {
        return new Prompt(text, Map.of());
    }
}
