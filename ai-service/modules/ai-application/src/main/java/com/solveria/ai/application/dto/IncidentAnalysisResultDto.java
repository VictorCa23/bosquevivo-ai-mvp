package com.solveria.ai.application.dto;

public record IncidentAnalysisResultDto(
        int priorityScore,
        int slaHours,
        String priorityReason,
        String recommendedAction,
        String model) {}
