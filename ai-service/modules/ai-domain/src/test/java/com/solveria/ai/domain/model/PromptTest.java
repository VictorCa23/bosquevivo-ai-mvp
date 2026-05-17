package com.solveria.ai.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class PromptTest {

    @Test
    void of_createsPromptWithEmptyMetadata() {
        var p = Prompt.of("hello");
        assertEquals("hello", p.text());
        assertTrue(p.metadata().isEmpty());
    }

    @Test
    void constructor_rejectsBlankText() {
        assertThrows(IllegalArgumentException.class, () -> new Prompt("  ", Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new Prompt(null, Map.of()));
    }
}
