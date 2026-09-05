package br.com.nevvesdev.openfinance.adapter.in.web.controller;

import br.com.nevvesdev.openfinance.adapter.out.security.JwtTokenIssuer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final JwtTokenIssuer tokenIssuer;

    public TokenController(JwtTokenIssuer tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    @PostMapping("/token")
    public Map<String, String> token(
            @RequestParam String clientId,
            @RequestParam(defaultValue = "consents:write consents:read") String scope
    ) throws Exception {
        var scopes = List.of(scope.split(" "));
        var token = tokenIssuer.issueToken(clientId, scopes);
        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", "3600",
                "scope", scope
        );
    }
}