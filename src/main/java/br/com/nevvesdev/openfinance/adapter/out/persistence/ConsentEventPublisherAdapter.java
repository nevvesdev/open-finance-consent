package br.com.nevvesdev.openfinance.adapter.out.persistence;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentEventJpaEntity;
import br.com.nevvesdev.openfinance.adapter.out.persistence.repository.ConsentEventJpaRepository;
import br.com.nevvesdev.openfinance.application.consent.ConsentEventPublisher;
import br.com.nevvesdev.openfinance.domain.consent.events.ConsentStatusChangedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsentEventPublisherAdapter implements ConsentEventPublisher {

    private final ConsentEventJpaRepository eventRepository;

    public ConsentEventPublisherAdapter(ConsentEventJpaRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void publish(List<ConsentStatusChangedEvent> events) {
        events.forEach(event -> {
            var entity = new ConsentEventJpaEntity();
            entity.setConsentId(event.consentId());
            entity.setPreviousStatus(event.previousStatus());
            entity.setNewStatus(event.newStatus());
            entity.setReason(event.reason());
            entity.setOccurredAt(event.occurredAt());
            eventRepository.save(entity);
        });
    }
}