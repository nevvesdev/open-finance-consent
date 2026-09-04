package br.com.nevvesdev.openfinance.adapter.in.web.dto;

import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.OffsetDateTime;
import java.util.Set;

public record CreateConsentRequest(
        @NotBlank String loggedUserCpf,
        String businessEntityCnpj,
        @NotEmpty Set<ConsentPermission> permissions,
        @Future OffsetDateTime expirationDateTime
) {}