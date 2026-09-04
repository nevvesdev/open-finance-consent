package br.com.nevvesdev.openfinance.adapter.out.persistence.repository;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsentEventJpaRepository extends JpaRepository<ConsentEventJpaEntity, UUID> {
}