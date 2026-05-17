package com.solveria.ai.infrastructure.llm.stub;

import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.port.out.LlmChatPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Stub LlmChatPort for dev and test profiles (no OpenAI calls). Returns deterministic responses for
 * development and testing. Only created if no other LlmChatPort bean exists.
 */
@Component
@Profile({"dev", "test"})
@ConditionalOnMissingBean(LlmChatPort.class)
public class StubLlmChatAdapter implements LlmChatPort {

    @Override
    public ChatResultDto chat(String prompt) {
        return new ChatResultDto(
                "[DEV-STUB] LLM disabled. Set OPENAI_API_KEY to enable real completions.", 0, 0);
    }
}
