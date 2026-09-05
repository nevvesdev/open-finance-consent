package br.com.nevvesdev.openfinance.application.consent;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import br.com.nevvesdev.openfinance.domain.consent.exception.ConsentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ExtendConsentUseCase {

    private final ConsentRepository consentRepository;
    private final ConsentEventPublisher eventPublisher;

    public ExtendConsentUseCase(ConsentRepository consentRepository,
                                ConsentEventPublisher eventPublisher) {
        this.consentRepository = consentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Consent execute(UUID consentId, OffsetDateTime newExpiration) {
        var consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ConsentException("Consent not found: " + consentId));

        consent.extend(newExpiration);

        var saved = consentRepository.save(consent);
        eventPublisher.publish(consent.pullDomainEvents());
        return saved;
    }
}