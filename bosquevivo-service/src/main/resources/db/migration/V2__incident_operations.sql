ALTER TABLE incidents DROP CONSTRAINT chk_incident_status;

ALTER TABLE incidents ADD COLUMN severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM';
ALTER TABLE incidents ADD COLUMN priority_score INTEGER NOT NULL DEFAULT 0;
ALTER TABLE incidents ADD COLUMN priority_reason TEXT;
ALTER TABLE incidents ADD COLUMN sla_due_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE incidents ADD COLUMN assigned_brigade_id UUID;
ALTER TABLE incidents ADD COLUMN assigned_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE incidents ADD COLUMN attention_started_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE incidents ADD COLUMN closed_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE incidents ADD COLUMN closure_notes TEXT;

UPDATE incidents SET status = 'CREATED' WHERE status = 'REPORTED';
UPDATE incidents SET status = 'IN_ATTENTION' WHERE status = 'IN_REVIEW';
UPDATE incidents SET status = 'CLOSED' WHERE status = 'RESOLVED';

ALTER TABLE incidents ADD CONSTRAINT chk_incident_status
    CHECK (status IN ('CREATED', 'PRIORITIZED', 'ASSIGNED', 'IN_ATTENTION', 'CLOSED', 'REOPENED'));

ALTER TABLE incidents ADD CONSTRAINT chk_incident_severity
    CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'));

ALTER TABLE incidents ADD CONSTRAINT chk_incident_priority_score
    CHECK (priority_score >= 0 AND priority_score <= 100);

CREATE TABLE brigades (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    zone VARCHAR(120) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO brigades (id, name, zone, available) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Brigada Norte', 'Zona Norte', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'Brigada Centro', 'Zona Centro', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'Brigada Sur', 'Zona Sur', TRUE);

ALTER TABLE incidents ADD CONSTRAINT fk_incidents_assigned_brigade
    FOREIGN KEY (assigned_brigade_id) REFERENCES brigades (id);

CREATE TABLE incident_events (
    id UUID PRIMARY KEY,
    incident_id UUID NOT NULL,
    type VARCHAR(60) NOT NULL,
    detail TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_incident_events_incident FOREIGN KEY (incident_id) REFERENCES incidents (id) ON DELETE CASCADE,
    CONSTRAINT chk_incident_event_type CHECK (
        type IN (
            'INCIDENT_CREATED',
            'INCIDENT_UPDATED',
            'INCIDENT_PRIORITIZED',
            'BRIGADE_ASSIGNED',
            'ATTENTION_STARTED',
            'INCIDENT_CLOSED',
            'INCIDENT_REOPENED',
            'INCIDENT_DELETED'
        )
    )
);

CREATE INDEX idx_incidents_severity ON incidents (severity);
CREATE INDEX idx_incidents_assigned_brigade ON incidents (assigned_brigade_id);
CREATE INDEX idx_incident_events_incident_created_at ON incident_events (incident_id, created_at DESC);
