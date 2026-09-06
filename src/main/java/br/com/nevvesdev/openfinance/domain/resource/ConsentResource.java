package br.com.nevvesdev.openfinance.domain.resource;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ConsentResource {

    private final UUID id;
    private final UUID consentId;
    private final UUID resourceId;
    private final ResourceType type;
    private ResourceStatus status;
    private final OffsetDateTime createdAt;

    public static ConsentResource create(UUID consentId, UUID resourceId, ResourceType type) {
        return new ConsentResource(
                UUID.randomUUID(), consentId, resourceId,
                type, ResourceStatus.AVAILABLE, OffsetDateTime.now()
        );
    }

    public static ConsentResource reconstitute(UUID id, UUID consentId, UUID resourceId,
                                               ResourceType type, ResourceStatus status,
                                               OffsetDateTime createdAt) {
        return new ConsentResource(id, consentId, resourceId, type, status, createdAt);
    }

    private ConsentResource(UUID id, UUID consentId, UUID resourceId,
                            ResourceType type, ResourceStatus status,
                            OffsetDateTime createdAt) {
        this.id = id;
        this.consentId = consentId;
        this.resourceId = resourceId;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getConsentId() { return consentId; }
    public UUID getResourceId() { return resourceId; }
    public ResourceType getType() { return type; }
    public ResourceStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}