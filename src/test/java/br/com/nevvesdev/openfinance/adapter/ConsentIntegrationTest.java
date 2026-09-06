package br.com.nevvesdev.openfinance.adapter;

import br.com.nevvesdev.openfinance.adapter.out.persistence.repository.ConsentJpaRepository;
import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ConsentIntegrationTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ConsentJpaRepository consentJpaRepository;

    @BeforeEach
    void setUp() {
        consentJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("deve criar consentimento e retornar 201")
    void shouldCreateConsent() throws Exception {
        var body = Map.of(
                "businessEntityCnpj", "12345678000195",
                "permissions", Set.of("ACCOUNTS_READ", "ACCOUNTS_BALANCES_READ"),
                "expirationDateTime", OffsetDateTime.now().plusDays(30).toString()
        );

        var result = mockMvc.perform(post("/consents")
                        .with(jwt().jwt(j -> j
                                .subject("12345678901")
                                .claim("scope", "consents:write consents:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.consentId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("AWAITING_AUTHORISATION"))
                .andReturn();

        assertThat(consentJpaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("deve retornar 401 sem token")
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/consents/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("deve retornar 403 sem escopo de escrita")
    void shouldReturn403WithoutWriteScope() throws Exception {
        var body = Map.of(
                "businessEntityCnpj", "12345678000195",
                "permissions", Set.of("ACCOUNTS_READ"),
                "expirationDateTime", OffsetDateTime.now().plusDays(30).toString()
        );

        mockMvc.perform(post("/consents")
                        .with(jwt().jwt(j -> j
                                .subject("12345678901")
                                .claim("scope", "consents:read"))) // só read
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deve completar ciclo de vida: criar → autorizar → revogar")
    void shouldCompleteLifecycle() throws Exception {
        // Cria
        var body = Map.of(
                "businessEntityCnpj", "12345678000195",
                "permissions", Set.of("ACCOUNTS_READ"),
                "expirationDateTime", OffsetDateTime.now().plusDays(30).toString()
        );

        var createResult = mockMvc.perform(post("/consents")
                        .with(jwt().jwt(j -> j
                                .subject("12345678901")
                                .claim("scope", "consents:write consents:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = objectMapper.readTree(
                createResult.getResponse().getContentAsString());
        var consentId = responseBody.get("consentId").asText();

        // Autoriza
        mockMvc.perform(patch("/consents/" + consentId + "/authorise")
                        .with(jwt().jwt(j -> j
                                .subject("12345678901")
                                .claim("scope", "consents:write"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AUTHORISED"));

        // Revoga
        mockMvc.perform(delete("/consents/" + consentId)
                        .with(jwt().jwt(j -> j
                                .subject("12345678901")
                                .claim("scope", "consents:write"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVOKED"));

        // Verifica no banco
        var entity = consentJpaRepository.findById(
                java.util.UUID.fromString(consentId));
        assertThat(entity).isPresent();
        assertThat(entity.get().getStatus()).isEqualTo(ConsentStatus.REVOKED);
    }

    @Test
    @DisplayName("deve retornar 422 para permissions vazia")
    void shouldReturn422ForEmptyPermissions() throws Exception {
        var body = Map.of(
                "businessEntityCnpj", "12345678000195",
                "permissions", Set.of(),
                "expirationDateTime", OffsetDateTime.now().plusDays(30).toString()
        );

        mockMvc.perform(post("/consents")
                        .with(jwt().jwt(j -> j
                                .subject("12345678901")
                                .claim("scope", "consents:write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}