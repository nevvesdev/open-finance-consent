package br.com.nevvesdev.openfinance.adapter.out.persistence.entity;

import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "consents")
public class ConsentJpaEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status;

    @Column(name = "logged_user_cpf", nullable = false)
    private String loggedUserCpf;

    @Column(name = "business_entity_cnpj")
    private String businessEntityCnpj;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "consent_permissions",
            joinColumns = @JoinColumn(name = "consent_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Set<ConsentPermission> permissions;

    @Column(name = "expiration_date_time", nullable = false)
    private OffsetDateTime expirationDateTime;

    @Column(name = "creation_date_time", nullable = false)
    private OffsetDateTime creationDateTime;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ConsentStatus getStatus() { return status; }
    public void setStatus(ConsentStatus status) { this.status = status; }
    public String getLoggedUserCpf() { return loggedUserCpf; }
    public void setLoggedUserCpf(String loggedUserCpf) { this.loggedUserCpf = loggedUserCpf; }
    public String getBusinessEntityCnpj() { return businessEntityCnpj; }
    public void setBusinessEntityCnpj(String businessEntityCnpj) { this.businessEntityCnpj = businessEntityCnpj; }
    public Set<ConsentPermission> getPermissions() { return permissions; }
    public void setPermissions(Set<ConsentPermission> permissions) { this.permissions = permissions; }
    public OffsetDateTime getExpirationDateTime() { return expirationDateTime; }
    public void setExpirationDateTime(OffsetDateTime expirationDateTime) { this.expirationDateTime = expirationDateTime; }
    public OffsetDateTime getCreationDateTime() { return creationDateTime; }
    public void setCreationDateTime(OffsetDateTime creationDateTime) { this.creationDateTime = creationDateTime; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}