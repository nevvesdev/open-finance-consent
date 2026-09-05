package br.com.nevvesdev.openfinance.adapter.in.web.dto;

import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Set;

public record CreateConsentRequest(
        String businessEntityCnpj,
        @NotEmpty Set<ConsentPermission> permissions,
        @NotNull @Future OffsetDateTime expirationDateTime
) {}