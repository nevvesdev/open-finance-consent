package br.com.nevvesdev.openfinance.adapter.in.web.controller;

import br.com.nevvesdev.openfinance.application.aggregation.AggregateAccountsUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    private final AggregateAccountsUseCase aggregateAccountsUseCase;

    public AggregationController(AggregateAccountsUseCase aggregateAccountsUseCase) {
        this.aggregateAccountsUseCase = aggregateAccountsUseCase;
    }

    @GetMapping("/consents/{consentId}/data")
    public Map<String, Object> aggregate(
            @PathVariable UUID consentId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return aggregateAccountsUseCase.execute(consentId);
    }
}