package br.com.nevvesdev.openfinance.application.consent;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import br.com.nevvesdev.openfinance.domain.consent.exception.ConsentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RejectConsentUseCase {

    private final ConsentRepository consentRepository;
    private final ConsentEventPublisher eventPublisher;

    public RejectConsentUseCase(ConsentRepository consentRepository,
                                ConsentEventPublisher eventPublisher) {
        this.consentRepository = consentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Consent execute(UUID consentId, String reason) {
        var consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ConsentException("Consent not found: " + consentId));

        consent.reject(reason);

        var saved = consentRepository.save(consent);
        eventPublisher.publish(consent.pullDomainEvents());
        return saved;
    }
}