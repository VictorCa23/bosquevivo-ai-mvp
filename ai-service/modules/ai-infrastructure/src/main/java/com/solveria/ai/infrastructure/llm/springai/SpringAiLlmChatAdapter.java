package com.solveria.ai.infrastructure.llm.springai;

import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.port.out.LlmChatPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * LLM chat adapter via Spring AI ChatClient. Returns answer + usage. Only created when OpenAI API
 * key is configured and ChatClient.Builder is available.
 */
@Component
@Profile("!test")
@ConditionalOnBean(ChatClient.Builder.class)
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class SpringAiLlmChatAdapter implements LlmChatPort {

    private final ChatClient chatClient;

    public SpringAiLlmChatAdapter(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public ChatResultDto chat(String prompt) {
        ChatResponse response = chatClient.prompt().user(prompt).call().chatResponse();
        String answer =
                response.getResult() != null && response.getResult().getOutput() != null
                        ? response.getResult().getOutput().getText()
                        : "";
        Usage usage =
                response.getMetadata() != null && response.getMetadata().getUsage() != null
                        ? response.getMetadata().getUsage()
                        : null;
        int promptTokens =
                usage != null ? (usage.getPromptTokens() != null ? usage.getPromptTokens() : 0) : 0;
        int completionTokens =
                usage != null
                        ? (usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0)
                        : 0;
        return new ChatResultDto(answer, promptTokens, completionTokens);
    }
}
