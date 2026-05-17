package com.solveria.ai.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.solveria.ai.application.port.out.LlmPort;
import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletePromptServiceTest {

    @Mock private LlmPort llmPort;

    @InjectMocks private CompletePromptService service;

    @Test
    void complete_delegatesToLlmPort() {
        var prompt = Prompt.of("hi");
        var completion = new Completion("hello", "stub", 1);
        when(llmPort.complete(any(Prompt.class))).thenReturn(completion);

        var result = service.complete(prompt);

        assertEquals("hello", result.content());
        assertEquals("stub", result.model());
    }
}
