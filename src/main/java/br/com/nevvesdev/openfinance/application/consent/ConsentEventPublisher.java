package br.com.nevvesdev.openfinance.application.consent;

import br.com.nevvesdev.openfinance.domain.consent.events.ConsentStatusChangedEvent;

import java.util.List;

public interface ConsentEventPublisher {
    void publish(List<ConsentStatusChangedEvent> events);
}