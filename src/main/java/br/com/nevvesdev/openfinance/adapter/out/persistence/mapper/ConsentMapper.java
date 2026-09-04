package br.com.nevvesdev.openfinance.adapter.out.persistence.mapper;

import br.com.nevvesdev.openfinance.adapter.out.persistence.entity.ConsentJpaEntity;
import br.com.nevvesdev.openfinance.domain.consent.Consent;
import org.springframework.stereotype.Component;

@Component
public class ConsentMapper {

    public ConsentJpaEntity toJpa(Consent consent) {
        var entity = new ConsentJpaEntity();
        entity.setId(consent.getId());
        entity.setStatus(consent.getStatus());
        entity.setLoggedUserCpf(consent.getLoggedUserCpf());
        entity.setBusinessEntityCnpj(consent.getBusinessEntityCnpj());
        entity.setPermissions(consent.getPermissions());
        entity.setExpirationDateTime(consent.getExpirationDateTime());
        entity.setCreationDateTime(consent.getCreationDateTime());
        return entity;
    }

    public Consent toDomain(ConsentJpaEntity entity) {
        return Consent.reconstitute(
                entity.getId(),
                entity.getStatus(),
                entity.getLoggedUserCpf(),
                entity.getBusinessEntityCnpj(),
                entity.getPermissions(),
                entity.getExpirationDateTime(),
                entity.getCreationDateTime()
        );
    }
}