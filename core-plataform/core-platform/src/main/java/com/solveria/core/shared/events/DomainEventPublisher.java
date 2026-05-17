package com.solveria.core.shared.events;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
