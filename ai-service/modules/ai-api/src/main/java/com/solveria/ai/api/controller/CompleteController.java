package com.solveria.ai.api.controller;

import com.solveria.ai.api.request.CompleteRequest;
import com.solveria.ai.api.response.CompleteResponse;
import com.solveria.ai.application.port.in.CompletePromptUseCase;
import com.solveria.ai.domain.model.Completion;
import com.solveria.ai.domain.model.Prompt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller for prompt completion. */
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Completion", description = "AI prompt completion endpoints")
public class CompleteController {

    private final CompletePromptUseCase completeUseCase;

    public CompleteController(CompletePromptUseCase completeUseCase) {
        this.completeUseCase = completeUseCase;
    }

    @PostMapping("/complete")
    @Operation(
            summary = "Complete a prompt",
            description =
                    "Completes a text prompt using AI. Requires JWT Bearer token with scope: ai.complete",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CompleteResponse> complete(@Valid @RequestBody CompleteRequest request) {
        var prompt = Prompt.of(request.prompt());
        Completion c = completeUseCase.complete(prompt);
        return ResponseEntity.ok(new CompleteResponse(c.content(), c.model(), c.tokensUsed()));
    }
}
