package br.com.nevvesdev.openfinance.domain.consent.events;

import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConsentStatusChangedEvent(
        UUID consentId,
        ConsentStatus previousStatus,
        ConsentStatus newStatus,
        String reason,
        OffsetDateTime occurredAt
) {
    public ConsentStatusChangedEvent(UUID consentId, ConsentStatus previousStatus,
                                     ConsentStatus newStatus, String reason) {
        this(consentId, previousStatus, newStatus, reason, OffsetDateTime.now());
    }
}