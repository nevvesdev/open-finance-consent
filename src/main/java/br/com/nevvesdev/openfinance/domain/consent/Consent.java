package br.com.nevvesdev.openfinance.domain.consent;

import br.com.nevvesdev.openfinance.domain.consent.exception.ConsentException;
import br.com.nevvesdev.openfinance.domain.consent.events.ConsentStatusChangedEvent;

import java.time.OffsetDateTime;
import java.util.*;

public class Consent {

    private final UUID id;
    private ConsentStatus status;
    private final String loggedUserCpf;
    private final String businessEntityCnpj;
    private final Set<ConsentPermission> permissions;
    private OffsetDateTime expirationDateTime;
    private final OffsetDateTime creationDateTime;
    private final List<ConsentStatusChangedEvent> domainEvents = new ArrayList<>();

    // Factory method — única forma de criar um novo consentimento
    public static Consent create(
            String loggedUserCpf,
            String businessEntityCnpj,
            Set<ConsentPermission> permissions,
            OffsetDateTime expirationDateTime
    ) {
        if (permissions == null || permissions.isEmpty()) {
            throw new ConsentException("Permissions cannot be empty");
        }
        if (expirationDateTime == null || expirationDateTime.isBefore(OffsetDateTime.now())) {
            throw new ConsentException("Expiration date must be in the future");
        }

        var consent = new Consent(
                UUID.randomUUID(),
                ConsentStatus.AWAITING_AUTHORISATION,
                loggedUserCpf,
                businessEntityCnpj,
                permissions,
                expirationDateTime,
                OffsetDateTime.now()
        );

        consent.registerEvent(null, ConsentStatus.AWAITING_AUTHORISATION, "Consent created");
        return consent;
    }

    // Reconstitui do banco — sem disparar eventos
    public static Consent reconstitute(
            UUID id,
            ConsentStatus status,
            String loggedUserCpf,
            String businessEntityCnpj,
            Set<ConsentPermission> permissions,
            OffsetDateTime expirationDateTime,
            OffsetDateTime creationDateTime
    ) {
        return new Consent(id, status, loggedUserCpf, businessEntityCnpj,
                permissions, expirationDateTime, creationDateTime);
    }

    private Consent(
            UUID id, ConsentStatus status, String loggedUserCpf,
            String businessEntityCnpj, Set<ConsentPermission> permissions,
            OffsetDateTime expirationDateTime, OffsetDateTime creationDateTime
    ) {
        this.id = id;
        this.status = status;
        this.loggedUserCpf = loggedUserCpf;
        this.businessEntityCnpj = businessEntityCnpj;
        this.permissions = Collections.unmodifiableSet(new HashSet<>(permissions));
        this.expirationDateTime = expirationDateTime;
        this.creationDateTime = creationDateTime;
    }

    // ── Comportamentos (máquina de estados) ──────────────────────────────────

    public void authorise() {
        assertStatus(ConsentStatus.AWAITING_AUTHORISATION, "Only AWAITING_AUTHORISATION consents can be authorised");
        transition(ConsentStatus.AUTHORISED, "Authorised by user");
    }

    public void reject(String reason) {
        assertStatus(ConsentStatus.AWAITING_AUTHORISATION, "Only AWAITING_AUTHORISATION consents can be rejected");
        transition(ConsentStatus.REJECTED, reason);
    }

    public void revoke(String reason) {
        if (status != ConsentStatus.AUTHORISED && status != ConsentStatus.AWAITING_AUTHORISATION) {
            throw new ConsentException("Cannot revoke a consent with status " + status);
        }
        transition(ConsentStatus.REVOKED, reason);
    }

    public void expire() {
        if (status == ConsentStatus.EXPIRED || status == ConsentStatus.REVOKED) {
            return;
        }
        transition(ConsentStatus.EXPIRED, "Expired by system");
    }

    public void extend(OffsetDateTime newExpiration) {
        assertStatus(ConsentStatus.AUTHORISED, "Only AUTHORISED consents can be extended");
        if (newExpiration.isBefore(OffsetDateTime.now())) {
            throw new ConsentException("New expiration must be in the future");
        }
        this.expirationDateTime = newExpiration;
        registerEvent(ConsentStatus.AUTHORISED, ConsentStatus.AUTHORISED, "Expiration extended");
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expirationDateTime);
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private void transition(ConsentStatus next, String reason) {
        ConsentStatus previous = this.status;
        this.status = next;
        registerEvent(previous, next, reason);
    }

    private void assertStatus(ConsentStatus expected, String message) {
        if (this.status != expected) {
            throw new ConsentException(message + ". Current status: " + this.status);
        }
    }

    private void registerEvent(ConsentStatus previous, ConsentStatus next, String reason) {
        domainEvents.add(new ConsentStatusChangedEvent(this.id, previous, next, reason));
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public ConsentStatus getStatus() { return status; }
    public String getLoggedUserCpf() { return loggedUserCpf; }
    public String getBusinessEntityCnpj() { return businessEntityCnpj; }
    public Set<ConsentPermission> getPermissions() { return permissions; }
    public OffsetDateTime getExpirationDateTime() { return expirationDateTime; }
    public OffsetDateTime getCreationDateTime() { return creationDateTime; }
    public List<ConsentStatusChangedEvent> pullDomainEvents() {
        var events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }
}