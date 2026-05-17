package com.solveria.ai.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.solveria.ai.application.dto.IncidentAnalysisCommandDto;
import org.junit.jupiter.api.Test;

class AnalyzeIncidentServiceTest {

    private final AnalyzeIncidentService service = new AnalyzeIncidentService();

    @Test
    void analyzesCriticalFireWithUrgentSla() {
        var result =
                service.analyze(
                        new IncidentAnalysisCommandDto(
                                "Fuego en reserva",
                                "Fuego visible cerca del bosque.",
                                "FIRE",
                                "CRITICAL",
                                -17.78,
                                -63.18));

        assertEquals(100, result.priorityScore());
        assertEquals(2, result.slaHours());
        assertEquals("bosquevivo-rules-mvp-0.7.1", result.model());
        assertTrue(result.recommendedAction().contains("Asignar brigada"));
    }

    @Test
    void analyzesLowOtherIncidentWithoutUrgentResponse() {
        var result =
                service.analyze(
                        new IncidentAnalysisCommandDto(
                                "Reporte menor", "", "OTHER", "LOW", -17.78, -63.18));

        assertEquals(30, result.priorityScore());
        assertEquals(24, result.slaHours());
        assertTrue(result.recommendedAction().contains("observar"));
    }
}
