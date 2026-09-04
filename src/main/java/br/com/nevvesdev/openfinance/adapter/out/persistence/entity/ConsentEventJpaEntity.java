package br.com.nevvesdev.openfinance.adapter.out.persistence.entity;

import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "consent_events")
public class ConsentEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "consent_id", nullable = false)
    private UUID consentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private ConsentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ConsentStatus newStatus;

    @Column
    private String reason;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getConsentId() { return consentId; }
    public void setConsentId(UUID consentId) { this.consentId = consentId; }
    public ConsentStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(ConsentStatus previousStatus) { this.previousStatus = previousStatus; }
    public ConsentStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ConsentStatus newStatus) { this.newStatus = newStatus; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
}