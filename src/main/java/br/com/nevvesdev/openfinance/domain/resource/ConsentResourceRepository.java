package br.com.nevvesdev.openfinance.domain.resource;

import java.util.List;
import java.util.UUID;

public interface ConsentResourceRepository {
    ConsentResource save(ConsentResource resource);
    List<ConsentResource> findByConsentId(UUID consentId);
}