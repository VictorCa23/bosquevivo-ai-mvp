package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.domain.IncidentSeverity;
import com.solveria.bosquevivo.incidents.domain.IncidentType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateIncidentRequest(
        @NotBlank @Size(max = 140) String title,
        @Size(max = 2000) String description,
        @NotNull IncidentType type,
        @NotNull IncidentSeverity severity,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude) {}
