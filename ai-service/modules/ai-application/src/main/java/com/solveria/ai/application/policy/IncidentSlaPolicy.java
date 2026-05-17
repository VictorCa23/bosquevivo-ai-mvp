package com.solveria.ai.application.policy;

public class IncidentSlaPolicy {

    public int hoursForScore(int score) {
        if (score >= 85) {
            return 2;
        }
        if (score >= 65) {
            return 6;
        }
        if (score >= 45) {
            return 12;
        }
        return 24;
    }
}
