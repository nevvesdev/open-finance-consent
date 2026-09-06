package br.com.nevvesdev.openfinance.adapter.out.persistence;

import br.com.nevvesdev.openfinance.adapter.out.persistence.repository.ConsentResourceJpaRepository;
import br.com.nevvesdev.openfinance.domain.resource.ConsentResource;
import br.com.nevvesdev.openfinance.domain.resource.ConsentResourceRepository;
import br.com.nevvesdev.openfinance.domain.resource.ResourceStatus;
import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentResourceJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConsentResourceRepositoryAdapter implements ConsentResourceRepository {

    private final ConsentResourceJpaRepository jpaRepository;

    public ConsentResourceRepositoryAdapter(ConsentResourceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConsentResource save(ConsentResource resource) {
        var entity = new ConsentResourceJpaEntity();
        entity.setId(resource.getId());
        entity.setConsentId(resource.getConsentId());
        entity.setResourceId(resource.getResourceId());
        entity.setType(resource.getType());
        entity.setStatus(resource.getStatus());
        entity.setCreatedAt(resource.getCreatedAt());
        jpaRepository.save(entity);
        return resource;
    }

    @Override
    public List<ConsentResource> findByConsentId(UUID consentId) {
        return jpaRepository.findByConsentId(consentId)
                .stream()
                .map(e -> ConsentResource.reconstitute(
                        e.getId(), e.getConsentId(), e.getResourceId(),
                        e.getType(), e.getStatus(), e.getCreatedAt()
                ))
                .toList();
    }
}