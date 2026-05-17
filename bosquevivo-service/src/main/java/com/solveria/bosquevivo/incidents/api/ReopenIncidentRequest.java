package com.solveria.bosquevivo.incidents.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReopenIncidentRequest(@NotBlank @Size(max = 1000) String reason) {}
