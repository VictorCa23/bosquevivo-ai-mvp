package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.domain.Incident;
import com.solveria.bosquevivo.incidents.domain.IncidentSeverity;
import com.solveria.bosquevivo.incidents.domain.IncidentStatus;
import com.solveria.bosquevivo.incidents.domain.IncidentType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String title,
        String description,
        IncidentType type,
        IncidentStatus status,
        IncidentSeverity severity,
        int priorityScore,
        String priorityReason,
        OffsetDateTime slaDueAt,
        UUID assignedBrigadeId,
        OffsetDateTime assignedAt,
        OffsetDateTime attentionStartedAt,
        OffsetDateTime closedAt,
        String closureNotes,
        double latitude,
        double longitude,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

    public static IncidentResponse from(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getType(),
                incident.getStatus(),
                incident.getSeverity(),
                incident.getPriorityScore(),
                incident.getPriorityReason(),
                incident.getSlaDueAt(),
                incident.getAssignedBrigadeId(),
                incident.getAssignedAt(),
                incident.getAttentionStartedAt(),
                incident.getClosedAt(),
                incident.getClosureNotes(),
                incident.getLatitude(),
                incident.getLongitude(),
                incident.getCreatedAt(),
                incident.getUpdatedAt());
    }
}
