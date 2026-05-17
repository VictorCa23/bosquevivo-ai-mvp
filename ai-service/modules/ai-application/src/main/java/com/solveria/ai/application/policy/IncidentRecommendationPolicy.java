package com.solveria.ai.application.policy;

public class IncidentRecommendationPolicy {

    public String actionForScore(int score) {
        if (score >= 85) {
            return "Asignar brigada inmediatamente y monitorear evolucion del riesgo.";
        }
        if (score >= 65) {
            return "Priorizar atencion operativa y preparar asignacion de brigada.";
        }
        if (score >= 45) {
            return "Mantener seguimiento y validar informacion del reporte.";
        }
        return "Registrar y observar sin activar respuesta urgente.";
    }
}
