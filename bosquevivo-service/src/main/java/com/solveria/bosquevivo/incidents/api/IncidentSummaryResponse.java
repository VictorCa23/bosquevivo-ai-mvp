package com.solveria.bosquevivo.incidents.api;

public record IncidentSummaryResponse(
        long total,
        long open,
        long prioritized,
        long assigned,
        long inAttention,
        long closed,
        long critical) {}
