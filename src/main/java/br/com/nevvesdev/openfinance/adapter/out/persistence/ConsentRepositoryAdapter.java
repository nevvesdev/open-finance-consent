package br.com.nevvesdev.openfinance.adapter.out.persistence;

import br.com.nevvesdev.openfinance.adapter.out.persistence.mapper.ConsentMapper;
import br.com.nevvesdev.openfinance.adapter.out.persistence.repository.ConsentJpaRepository;
import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ConsentRepositoryAdapter implements ConsentRepository {

    private final ConsentJpaRepository jpaRepository;
    private final ConsentMapper mapper;

    public ConsentRepositoryAdapter(ConsentJpaRepository jpaRepository, ConsentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Consent save(Consent consent) {
        var entity = mapper.toJpa(consent);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Consent> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}