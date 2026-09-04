package br.com.nevvesdev.openfinance.application.consent;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import br.com.nevvesdev.openfinance.domain.consent.exception.ConsentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetConsentUseCase {

    private final ConsentRepository consentRepository;

    public GetConsentUseCase(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    @Transactional(readOnly = true)
    public Consent execute(UUID id) {
        return consentRepository.findById(id)
                .orElseThrow(() -> new ConsentException("Consent not found: " + id));
    }
}