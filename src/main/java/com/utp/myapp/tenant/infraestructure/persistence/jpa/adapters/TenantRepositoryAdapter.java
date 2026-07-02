package com.utp.myapp.tenant.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import com.utp.myapp.tenant.infraestructure.persistence.jpa.entities.TenantEntity;
import com.utp.myapp.tenant.infraestructure.persistence.jpa.mappers.TenantMapper;
import com.utp.myapp.tenant.infraestructure.persistence.jpa.repositories.JPATenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TenantRepositoryAdapter implements ITenantRepository {

    private final JPATenantRepository jpa;
    private final TenantMapper mapper;

    @Override
    public Tenant save(Tenant tenant) {
        TenantEntity entity = mapper.toEntity(tenant);
        TenantEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Tenant> findById(TenantId id) {
        return jpa.findAll().stream()
                .filter(e -> e.getTenantId().equals(id.value()))
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(TenantId id) {
        return jpa.findAll().stream()
                .anyMatch(e -> e.getTenantId().equals(id.value()));
    }
}
