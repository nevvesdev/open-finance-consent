package br.com.nevvesdev.openfinance.application.consent;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class CreateConsentUseCase {

    private final ConsentRepository consentRepository;
    private final ConsentEventPublisher eventPublisher;

    public CreateConsentUseCase(ConsentRepository consentRepository,
                                ConsentEventPublisher eventPublisher) {
        this.consentRepository = consentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Consent execute(String loggedUserCpf, String businessEntityCnpj,
                           Set<ConsentPermission> permissions,
                           OffsetDateTime expirationDateTime) {
        var consent = Consent.create(loggedUserCpf, businessEntityCnpj,
                permissions, expirationDateTime);

        var saved = consentRepository.save(consent);
        eventPublisher.publish(saved.pullDomainEvents());
        return saved;
    }
}