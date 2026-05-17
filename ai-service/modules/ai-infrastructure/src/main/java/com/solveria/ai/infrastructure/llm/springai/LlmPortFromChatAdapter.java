package com.solveria.ai.infrastructure.llm.springai;

import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.port.out.LlmChatPort;
import com.solveria.ai.application.port.out.LlmPort;
import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/** LlmPort adapter that delegates to LlmChatPort (e.g. Spring AI ChatClient or stub). */
@Component
@ConditionalOnBean(LlmChatPort.class)
public class LlmPortFromChatAdapter implements LlmPort {

    private static final String MODEL_NAME = "openai";

    private final LlmChatPort llmChat;

    public LlmPortFromChatAdapter(LlmChatPort llmChat) {
        this.llmChat = llmChat;
    }

    @Override
    public Completion complete(Prompt prompt) {
        ChatResultDto result = llmChat.chat(prompt.text());
        return new Completion(result.answer(), MODEL_NAME, result.totalTokens());
    }
}
