package com.solveria.ai.infrastructure.llm.springai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.port.out.LlmChatPort;
import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LlmPortFromChatAdapterTest {

    @Mock private LlmChatPort llmChat;

    @Test
    void complete_delegatesToLlmChatAndMapsToCompletion() {
        when(llmChat.chat("hello")).thenReturn(new ChatResultDto("hi", 1, 2));
        var adapter = new LlmPortFromChatAdapter(llmChat);
        var prompt = Prompt.of("hello");

        Completion c = adapter.complete(prompt);

        assertEquals("hi", c.content());
        assertEquals("openai", c.model());
        assertEquals(3, c.tokensUsed());
        verify(llmChat).chat("hello");
    }
}
