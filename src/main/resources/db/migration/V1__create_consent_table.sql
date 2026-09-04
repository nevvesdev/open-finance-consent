CREATE TABLE consents (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status              VARCHAR(30)  NOT NULL,
    logged_user_cpf     VARCHAR(11)  NOT NULL,
    business_entity_cnpj VARCHAR(14),
    expiration_date_time TIMESTAMPTZ NOT NULL,
    creation_date_time  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE consent_permissions (
    consent_id UUID        NOT NULL REFERENCES consents(id) ON DELETE CASCADE,
    permission VARCHAR(60) NOT NULL,
    PRIMARY KEY (consent_id, permission)
);

CREATE TABLE consent_events (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    consent_id  UUID        NOT NULL REFERENCES consents(id),
    previous_status VARCHAR(30),
    new_status  VARCHAR(30)  NOT NULL,
    reason      VARCHAR(255),
    occurred_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_consents_status ON consents(status);
CREATE INDEX idx_consent_events_consent_id ON consent_events(consent_id);