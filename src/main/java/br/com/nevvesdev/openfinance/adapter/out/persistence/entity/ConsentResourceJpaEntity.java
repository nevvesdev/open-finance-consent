package br.com.nevvesdev.openfinance.adapter.out.persistence.entity;

import br.com.nevvesdev.openfinance.domain.resource.ResourceStatus;
import br.com.nevvesdev.openfinance.domain.resource.ResourceType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "consent_resources")
public class ConsentResourceJpaEntity {

    @Id
    private UUID id;

    @Column(name = "consent_id", nullable = false)
    private UUID consentId;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getConsentId() { return consentId; }
    public void setConsentId(UUID consentId) { this.consentId = consentId; }
    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }
    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}