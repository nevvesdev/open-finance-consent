package br.com.nevvesdev.openfinance.adapter.out.persistence.repository;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.OutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
    List<OutboxEventJpaEntity> findByStatusOrderByCreatedAtAsc(String status);
}