package com.solveria.ai.application.port.in;

import com.solveria.ai.application.dto.RagQaCommandDto;
import com.solveria.ai.application.dto.RagQaResultDto;

/** Port in: RAG QA use case (vector search + LLM with tenant/namespace filter). */
public interface RagQaUseCase {

    RagQaResultDto ask(RagQaCommandDto command);
}
