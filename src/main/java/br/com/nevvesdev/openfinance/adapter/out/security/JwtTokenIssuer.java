package br.com.nevvesdev.openfinance.adapter.out.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenIssuer {

    private final RSAPrivateKey privateKey;

    public JwtTokenIssuer(@Value("classpath:certs/jwt-private.pem") Resource privateKeyResource)
            throws Exception {
        String pem = privateKeyResource.getContentAsString(StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(pem);
        var spec = new PKCS8EncodedKeySpec(keyBytes);
        this.privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public String issueToken(String clientId, List<String> scopes) throws Exception {
        var now = new Date();
        var expiry = new Date(now.getTime() + 3_600_000); // 1 hora

        var claims = new JWTClaimsSet.Builder()
                .issuer("https://auth.openfinance.nevvesdev.com.br")
                .subject(clientId)
                .audience("open-finance-consent-hub")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(now)
                .expirationTime(expiry)
                .claim("scope", String.join(" ", scopes))
                .claim("client_id", clientId)
                .build();

        var header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID("openfinance-key-1")
                .build();

        var jwt = new SignedJWT(header, claims);
        jwt.sign(new RSASSASigner(privateKey));
        return jwt.serialize();
    }
}