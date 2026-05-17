package com.solveria.ai.application.dto;

public record IncidentAnalysisCommandDto(
        String title,
        String description,
        String type,
        String severity,
        double latitude,
        double longitude) {}
