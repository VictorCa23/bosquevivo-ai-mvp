package com.solveria.core.observability.metrics;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class UseCaseMetricsService {

    private final ObservationRegistry registry;

    public UseCaseMetricsService(ObservationRegistry registry) {
        this.registry = registry;
    }

    public void observe(String useCaseName, Runnable action) {
        Observation.createNotStarted(useCaseName, registry).observe(action);
    }
}
