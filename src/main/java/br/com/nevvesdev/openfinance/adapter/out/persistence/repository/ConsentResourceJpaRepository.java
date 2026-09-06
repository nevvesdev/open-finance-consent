package br.com.nevvesdev.openfinance.adapter.out.persistence.repository;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentResourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsentResourceJpaRepository extends JpaRepository<ConsentResourceJpaEntity, UUID> {
    List<ConsentResourceJpaEntity> findByConsentId(UUID consentId);
}