package br.com.nevvesdev.openfinance.adapter.out.persistence;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentEventJpaEntity;
import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.OutboxEventJpaEntity;
import br.com.nevvesdev.openfinance.adapter.out.persistence.repository.ConsentEventJpaRepository;
import br.com.nevvesdev.openfinance.adapter.out.persistence.repository.OutboxEventJpaRepository;
import br.com.nevvesdev.openfinance.application.consent.ConsentEventPublisher;
import br.com.nevvesdev.openfinance.domain.consent.events.ConsentStatusChangedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsentEventPublisherAdapter implements ConsentEventPublisher {

    private final ConsentEventJpaRepository eventRepository;
    private final OutboxEventJpaRepository outboxRepository;

    public ConsentEventPublisherAdapter(ConsentEventJpaRepository eventRepository,
                                        OutboxEventJpaRepository outboxRepository) {
        this.eventRepository = eventRepository;
        this.outboxRepository = outboxRepository;
    }

    @Override
    public void publish(List<ConsentStatusChangedEvent> events) {
        events.forEach(event -> {
            persistAuditEvent(event);
            persistOutboxEvent(event);
        });
    }

    private void persistAuditEvent(ConsentStatusChangedEvent event) {
        var entity = new ConsentEventJpaEntity();
        entity.setConsentId(event.consentId());
        entity.setPreviousStatus(event.previousStatus());
        entity.setNewStatus(event.newStatus());
        entity.setReason(event.reason());
        entity.setOccurredAt(event.occurredAt());
        eventRepository.save(entity);
    }

    private void persistOutboxEvent(ConsentStatusChangedEvent event) {
        var outbox = new OutboxEventJpaEntity();
        outbox.setAggregateType("Consent");
        outbox.setAggregateId(event.consentId());
        outbox.setEventType("ConsentStatusChanged");
        outbox.setPayload(buildPayload(event));
        outboxRepository.save(outbox);
    }

    private String buildPayload(ConsentStatusChangedEvent event) {
        return """
                {
                  "consentId": "%s",
                  "previousStatus": "%s",
                  "newStatus": "%s",
                  "reason": "%s",
                  "occurredAt": "%s"
                }
                """.formatted(
                event.consentId(),
                event.previousStatus(),
                event.newStatus(),
                event.reason(),
                event.occurredAt()
        );
    }
}