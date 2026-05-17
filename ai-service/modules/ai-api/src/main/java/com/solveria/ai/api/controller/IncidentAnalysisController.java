package com.solveria.ai.api.controller;

import com.solveria.ai.api.request.IncidentAnalysisRequest;
import com.solveria.ai.api.response.IncidentAnalysisResponse;
import com.solveria.ai.application.dto.IncidentAnalysisCommandDto;
import com.solveria.ai.application.dto.IncidentAnalysisResultDto;
import com.solveria.ai.application.port.in.AnalyzeIncidentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incident Analysis", description = "BosqueVivo incident analysis MVP")
public class IncidentAnalysisController {

    private final AnalyzeIncidentUseCase useCase;

    public IncidentAnalysisController(AnalyzeIncidentUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze an environmental incident")
    public ResponseEntity<IncidentAnalysisResponse> analyze(
            @Valid @RequestBody IncidentAnalysisRequest request) {
        IncidentAnalysisResultDto result =
                useCase.analyze(
                        new IncidentAnalysisCommandDto(
                                request.title(),
                                request.description(),
                                request.type(),
                                request.severity(),
                                request.latitude(),
                                request.longitude()));
        return ResponseEntity.ok(
                new IncidentAnalysisResponse(
                        result.priorityScore(),
                        result.slaHours(),
                        result.priorityReason(),
                        result.recommendedAction(),
                        result.model()));
    }
}
