package com.solveria.bosquevivo.incidents.api;

import com.solveria.bosquevivo.incidents.domain.Brigade;
import java.util.UUID;

public record BrigadeResponse(UUID id, String name, String zone, boolean available) {

    public static BrigadeResponse from(Brigade brigade) {
        return new BrigadeResponse(
                brigade.getId(), brigade.getName(), brigade.getZone(), brigade.isAvailable());
    }
}
