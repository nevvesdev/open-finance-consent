package br.com.nevvesdev.openfinance.adapter.out.persistence.repository;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentJpaEntity;
import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ConsentJpaRepository extends JpaRepository<ConsentJpaEntity, UUID> {

    @Query("""
            SELECT c FROM ConsentJpaEntity c
            WHERE c.expirationDateTime < :now
            AND c.status NOT IN (:terminalStatuses)
            """)
    List<ConsentJpaEntity> findExpirable(
            @Param("now") OffsetDateTime now,
            @Param("terminalStatuses") List<ConsentStatus> terminalStatuses
    );
}