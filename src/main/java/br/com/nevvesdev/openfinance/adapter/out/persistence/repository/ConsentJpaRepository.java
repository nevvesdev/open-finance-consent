package br.com.nevvesdev.openfinance.adapter.out.persistence.repository;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsentJpaRepository extends JpaRepository<ConsentJpaEntity, UUID> {
}