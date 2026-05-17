package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.domain.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeIncidentStatusRequest(@NotNull IncidentStatus status) {}
