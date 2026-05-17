package com.solveria.ai.api.controller;

import com.solveria.ai.api.request.RagQaRequest;
import com.solveria.ai.api.response.RagQaResponse;
import com.solveria.ai.application.dto.RagQaCommandDto;
import com.solveria.ai.application.dto.RagQaResultDto;
import com.solveria.ai.application.port.in.RagQaUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller for RAG QA. */
@RestController
@RequestMapping("/api/v1/ai")
public class RagQaController {

    private final RagQaUseCase ragQaUseCase;

    public RagQaController(RagQaUseCase ragQaUseCase) {
        this.ragQaUseCase = ragQaUseCase;
    }

    @PostMapping("/rag/qa")
    public ResponseEntity<RagQaResponse> qa(@Valid @RequestBody RagQaRequest request) {
        var command = new RagQaCommandDto(request.question(), request.namespace());
        RagQaResultDto result = ragQaUseCase.ask(command);
        return ResponseEntity.ok(
                new RagQaResponse(
                        result.answer(), result.promptTokens(), result.completionTokens()));
    }
}
