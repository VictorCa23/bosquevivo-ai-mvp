package com.solveria.core.shared.events;

import java.time.Instant;

/** Contrato base para todos los eventos de dominio. */
public interface DomainEvent {

    Instant occurredAt();
}
