package com.solveria.ai.application.policy;

public class IncidentRiskPolicy {

    public int score(String type, String severity) {
        return Math.min(typeScore(type) + severityScore(severity), 100);
    }

    private int typeScore(String type) {
        return switch (normalize(type)) {
            case "FIRE" -> 55;
            case "SMOKE" -> 40;
            case "ILLEGAL_LOGGING" -> 35;
            case "POLLUTION" -> 30;
            default -> 20;
        };
    }

    private int severityScore(String severity) {
        return switch (normalize(severity)) {
            case "CRITICAL" -> 45;
            case "HIGH" -> 35;
            case "MEDIUM" -> 20;
            default -> 10;
        };
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}
