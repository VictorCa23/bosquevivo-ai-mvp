package com.solveria.bosquevivo.incidents.application.ai;

import java.time.OffsetDateTime;

public record IncidentAnalysisResult(
        int score,
        String reason,
        OffsetDateTime slaDueAt,
        String recommendedAction,
        String model) {}
