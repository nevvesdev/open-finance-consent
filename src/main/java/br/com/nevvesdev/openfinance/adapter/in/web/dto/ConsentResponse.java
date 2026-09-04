package br.com.nevvesdev.openfinance.adapter.in.web.dto;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record ConsentResponse(
        UUID consentId,
        ConsentStatus status,
        String loggedUserCpf,
        String businessEntityCnpj,
        Set<ConsentPermission> permissions,
        OffsetDateTime expirationDateTime,
        OffsetDateTime creationDateTime
) {
    public static ConsentResponse from(Consent consent) {
        return new ConsentResponse(
                consent.getId(),
                consent.getStatus(),
                consent.getLoggedUserCpf(),
                consent.getBusinessEntityCnpj(),
                consent.getPermissions(),
                consent.getExpirationDateTime(),
                consent.getCreationDateTime()
        );
    }
}