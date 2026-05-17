package com.solveria.bosquevivo.incidents.api;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignIncidentRequest(@NotNull UUID brigadeId) {}
