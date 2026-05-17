package com.solveria.ai.application.port.out;

import com.solveria.ai.application.dto.ChatResultDto;

/** Port out: LLM chat (prompt in, generic ChatResult with answer + usage out). */
public interface LlmChatPort {

    ChatResultDto chat(String prompt);
}
