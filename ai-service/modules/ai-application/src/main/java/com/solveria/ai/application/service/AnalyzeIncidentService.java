package com.solveria.ai.application.service;

import com.solveria.ai.application.dto.IncidentAnalysisCommandDto;
import com.solveria.ai.application.dto.IncidentAnalysisResultDto;
import com.solveria.ai.application.policy.IncidentRecommendationPolicy;
import com.solveria.ai.application.policy.IncidentRiskPolicy;
import com.solveria.ai.application.policy.IncidentSlaPolicy;
import com.solveria.ai.application.port.in.AnalyzeIncidentUseCase;

public class AnalyzeIncidentService implements AnalyzeIncidentUseCase {

    private static final String MODEL = "bosquevivo-rules-mvp-0.7.1";

    private final IncidentRiskPolicy riskPolicy;
    private final IncidentSlaPolicy slaPolicy;
    private final IncidentRecommendationPolicy recommendationPolicy;

    public AnalyzeIncidentService() {
        this(new IncidentRiskPolicy(), new IncidentSlaPolicy(), new IncidentRecommendationPolicy());
    }

    public AnalyzeIncidentService(
            IncidentRiskPolicy riskPolicy,
            IncidentSlaPolicy slaPolicy,
            IncidentRecommendationPolicy recommendationPolicy) {
        this.riskPolicy = riskPolicy;
        this.slaPolicy = slaPolicy;
        this.recommendationPolicy = recommendationPolicy;
    }

    @Override
    public IncidentAnalysisResultDto analyze(IncidentAnalysisCommandDto command) {
        int score = riskPolicy.score(command.type(), command.severity());
        int slaHours = slaPolicy.hoursForScore(score);
        String reason =
                "Analisis AI MVP 0.7.1: prioridad "
                        + score
                        + "/100 por tipo "
                        + normalize(command.type())
                        + ", severidad "
                        + normalize(command.severity())
                        + " y reglas ambientales deterministicas. SLA sugerido: "
                        + slaHours
                        + " horas.";
        return new IncidentAnalysisResultDto(
                score, slaHours, reason, recommendationPolicy.actionForScore(score), MODEL);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}
