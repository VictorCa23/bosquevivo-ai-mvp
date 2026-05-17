package com.solveria.core.shared.exceptions;

import java.util.Map;

public class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException(String entity, String identifier) {
        super(
                "error.entity.not_found",
                Map.of(
                        "entity", entity,
                        "id", identifier),
                "Entity not found: " + entity + " with id " + identifier);
    }
}
