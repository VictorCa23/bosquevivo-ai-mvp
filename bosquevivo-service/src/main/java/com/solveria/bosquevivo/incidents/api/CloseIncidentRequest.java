package com.solveria.bosquevivo.incidents.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CloseIncidentRequest(
        @AssertTrue boolean areaSecured,
        @AssertTrue boolean riskControlled,
        @NotBlank @Size(max = 2000) String notes) {}
