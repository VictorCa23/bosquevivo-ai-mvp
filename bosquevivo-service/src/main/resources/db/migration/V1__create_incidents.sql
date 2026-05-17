CREATE TABLE incidents (
    id UUID PRIMARY KEY,
    title VARCHAR(140) NOT NULL,
    description TEXT,
    type VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_incident_type CHECK (type IN ('FIRE', 'SMOKE', 'ILLEGAL_LOGGING', 'POLLUTION', 'OTHER')),
    CONSTRAINT chk_incident_status CHECK (status IN ('REPORTED', 'IN_REVIEW', 'RESOLVED')),
    CONSTRAINT chk_incident_latitude CHECK (latitude >= -90 AND latitude <= 90),
    CONSTRAINT chk_incident_longitude CHECK (longitude >= -180 AND longitude <= 180)
);

CREATE INDEX idx_incidents_status ON incidents (status);
CREATE INDEX idx_incidents_created_at ON incidents (created_at DESC);
