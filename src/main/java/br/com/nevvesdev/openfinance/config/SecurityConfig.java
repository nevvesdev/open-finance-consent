package br.com.nevvesdev.openfinance.config;

import br.com.nevvesdev.openfinance.adapter.out.security.ConsentScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoint de token liberado (simula authorization server público)
                        .requestMatchers("/auth/token").permitAll()
                        // Criação e mutações exigem escopo de escrita
                        .requestMatchers(HttpMethod.POST, "/consents").hasAuthority(ConsentScope.CONSENTS_WRITE)
                        .requestMatchers(HttpMethod.PATCH, "/consents/**").hasAuthority(ConsentScope.CONSENTS_WRITE)
                        .requestMatchers(HttpMethod.DELETE, "/consents/**").hasAuthority(ConsentScope.CONSENTS_WRITE)
                        .requestMatchers(HttpMethod.POST, "/consents/*/extends").hasAuthority(ConsentScope.CONSENTS_WRITE)
                        // Consultas exigem escopo de leitura
                        .requestMatchers(HttpMethod.GET, "/consents/**").hasAuthority(ConsentScope.CONSENTS_READ)
                        .requestMatchers(HttpMethod.GET, "/aggregation/**").hasAuthority(ConsentScope.CONSENTS_READ)
                        .requestMatchers("/mock/**").permitAll() // mock banks liberados
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }
}