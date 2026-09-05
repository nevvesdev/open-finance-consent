package br.com.nevvesdev.openfinance.application.consent;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import br.com.nevvesdev.openfinance.domain.consent.exception.ConsentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthoriseConsentUseCase {

    private final ConsentRepository consentRepository;
    private final ConsentEventPublisher eventPublisher;

    public AuthoriseConsentUseCase(ConsentRepository consentRepository,
                                   ConsentEventPublisher eventPublisher) {
        this.consentRepository = consentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Consent execute(UUID consentId) {
        var consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ConsentException("Consent not found: " + consentId));

        consent.authorise();

        var saved = consentRepository.save(consent);
        eventPublisher.publish(consent.pullDomainEvents());
        return saved;
    }
}