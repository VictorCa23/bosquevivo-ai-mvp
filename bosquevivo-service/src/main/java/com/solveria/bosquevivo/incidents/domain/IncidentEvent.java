package com.solveria.bosquevivo.incidents.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "incident_events")
public class IncidentEvent {

    @Id private UUID id;

    @Column(nullable = false)
    private UUID incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private IncidentEventType type;

    @Column(nullable = false, columnDefinition = "text")
    private String detail;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected IncidentEvent() {}

    public IncidentEvent(UUID incidentId, IncidentEventType type, String detail) {
        this.id = UUID.randomUUID();
        this.incidentId = incidentId;
        this.type = type;
        this.detail = detail;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getIncidentId() {
        return incidentId;
    }

    public IncidentEventType getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
