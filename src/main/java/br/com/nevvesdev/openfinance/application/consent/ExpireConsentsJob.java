package br.com.nevvesdev.openfinance.application.consent;

import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
public class ExpireConsentsJob {

    private static final Logger log = LoggerFactory.getLogger(ExpireConsentsJob.class);

    private final ConsentRepository consentRepository;
    private final ConsentEventPublisher eventPublisher;

    public ExpireConsentsJob(ConsentRepository consentRepository,
                             ConsentEventPublisher eventPublisher) {
        this.consentRepository = consentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelayString = "${jobs.expire-consents.delay-ms:60000}")
    @Transactional
    public void run() {
        var expirable = consentRepository.findExpirable(OffsetDateTime.now());

        if (expirable.isEmpty()) return;

        log.info("ExpireConsentsJob: {} consent(s) to expire", expirable.size());

        expirable.forEach(consent -> {
            consent.expire();
            consentRepository.save(consent);
            eventPublisher.publish(consent.pullDomainEvents());
            log.debug("Expired consent {}", consent.getId());
        });
    }
}