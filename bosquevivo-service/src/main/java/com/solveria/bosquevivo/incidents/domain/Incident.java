package com.solveria.bosquevivo.incidents.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id private UUID id;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private IncidentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IncidentSeverity severity;

    @Column(nullable = false)
    private int priorityScore;

    @Column(columnDefinition = "text")
    private String priorityReason;

    @Column private OffsetDateTime slaDueAt;

    @Column private UUID assignedBrigadeId;

    @Column private OffsetDateTime assignedAt;

    @Column private OffsetDateTime attentionStartedAt;

    @Column private OffsetDateTime closedAt;

    @Column(columnDefinition = "text")
    private String closureNotes;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Version private long version;

    protected Incident() {}

    public Incident(
            String title,
            String description,
            IncidentType type,
            IncidentSeverity severity,
            double latitude,
            double longitude) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = IncidentStatus.CREATED;
        this.severity = severity;
        this.priorityScore = 0;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void changeStatus(IncidentStatus status) {
        this.status = status;
    }

    public void update(
            String title,
            String description,
            IncidentType type,
            IncidentSeverity severity,
            double latitude,
            double longitude) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.severity = severity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void prioritize(int priorityScore, String priorityReason, OffsetDateTime slaDueAt) {
        this.priorityScore = priorityScore;
        this.priorityReason = priorityReason;
        this.slaDueAt = slaDueAt;
        this.status = IncidentStatus.PRIORITIZED;
    }

    public void assign(UUID brigadeId) {
        this.assignedBrigadeId = brigadeId;
        this.assignedAt = OffsetDateTime.now();
        this.status = IncidentStatus.ASSIGNED;
    }

    public void startAttention() {
        this.attentionStartedAt = OffsetDateTime.now();
        this.status = IncidentStatus.IN_ATTENTION;
    }

    public void close(String closureNotes) {
        this.closureNotes = closureNotes;
        this.closedAt = OffsetDateTime.now();
        this.status = IncidentStatus.CLOSED;
    }

    public void reopen(String reason) {
        this.closureNotes = reason;
        this.closedAt = null;
        this.status = IncidentStatus.REOPENED;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IncidentType getType() {
        return type;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public String getPriorityReason() {
        return priorityReason;
    }

    public OffsetDateTime getSlaDueAt() {
        return slaDueAt;
    }

    public UUID getAssignedBrigadeId() {
        return assignedBrigadeId;
    }

    public OffsetDateTime getAssignedAt() {
        return assignedAt;
    }

    public OffsetDateTime getAttentionStartedAt() {
        return attentionStartedAt;
    }

    public OffsetDateTime getClosedAt() {
        return closedAt;
    }

    public String getClosureNotes() {
        return closureNotes;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
