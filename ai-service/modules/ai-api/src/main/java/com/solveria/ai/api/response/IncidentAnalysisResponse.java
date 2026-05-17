package com.solveria.ai.api.response;

public record IncidentAnalysisResponse(
        int priorityScore,
        int slaHours,
        String priorityReason,
        String recommendedAction,
        String model) {}
