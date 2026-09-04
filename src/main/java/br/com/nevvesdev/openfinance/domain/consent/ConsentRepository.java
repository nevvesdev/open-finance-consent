package br.com.nevvesdev.openfinance.domain.consent;

import java.util.Optional;
import java.util.UUID;

public interface ConsentRepository {
    Consent save(Consent consent);
    Optional<Consent> findById(UUID id);
}