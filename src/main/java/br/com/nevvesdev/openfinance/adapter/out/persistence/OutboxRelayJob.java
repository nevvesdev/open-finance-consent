package br.com.nevvesdev.openfinance.adapter.out.persistence;

import br.com.nevvesdev.openfinance.adapter.out.persistence.repository.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
public class OutboxRelayJob {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayJob.class);

    private final OutboxEventJpaRepository outboxRepository;

    public OutboxRelayJob(OutboxEventJpaRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedDelayString = "${jobs.outbox-relay.delay-ms:30000}")
    @Transactional
    public void relay() {
        var pending = outboxRepository.findByStatusOrderByCreatedAtAsc("PENDING");

        if (pending.isEmpty()) return;

        log.info("OutboxRelayJob: {} event(s) to relay", pending.size());

        pending.forEach(event -> {
            // Em produção: kafkaTemplate.send(topic, event.getPayload())
            log.info("Relaying event [{}] for aggregate [{}]: {}",
                    event.getEventType(), event.getAggregateId(), event.getPayload());

            event.setStatus("PROCESSED");
            event.setProcessedAt(OffsetDateTime.now());
            outboxRepository.save(event);
        });
    }
}