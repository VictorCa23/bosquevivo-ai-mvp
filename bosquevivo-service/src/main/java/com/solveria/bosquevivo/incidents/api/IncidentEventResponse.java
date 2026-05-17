package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.domain.IncidentEvent;
import com.solveria.bosquevivo.incidents.domain.IncidentEventType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record IncidentEventResponse(
        UUID id, UUID incidentId, IncidentEventType type, String detail, OffsetDateTime createdAt) {

    public static IncidentEventResponse from(IncidentEvent event) {
        return new IncidentEventResponse(
                event.getId(),
                event.getIncidentId(),
                event.getType(),
                event.getDetail(),
                event.getCreatedAt());
    }
}
