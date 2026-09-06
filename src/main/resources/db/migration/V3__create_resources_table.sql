CREATE TABLE consent_resources (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    consent_id  UUID        NOT NULL REFERENCES consents(id),
    resource_id UUID        NOT NULL,
    type        VARCHAR(30) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_consent_resources_consent_id ON consent_resources(consent_id);