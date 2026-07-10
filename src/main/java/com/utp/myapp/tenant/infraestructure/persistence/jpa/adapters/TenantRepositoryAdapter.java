package com.utp.myapp.tenant.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import com.utp.myapp.tenant.infraestructure.persistence.jpa.entities.TenantEntity;
import com.utp.myapp.tenant.infraestructure.persistence.jpa.mappers.TenantMapper;
import com.utp.myapp.tenant.infraestructure.persistence.jpa.repositories.JPATenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TenantRepositoryAdapter implements ITenantRepository {

    private final JPATenantRepository jpa;
    private final TenantMapper mapper;

    /**
     * Upsert by tenantId (string), not by primary key.
     *
     * The TenantEntity has an auto-generated Long id plus a separate tenantId
     * column used for multi-tenancy. Since the domain operates on tenantId,
     * we use it as the natural key here:
     *   - If a row with this tenantId exists → load, mutate, save.
     *   - Otherwise → insert.
     *
     * The previous version always called jpa.save() on a new entity built from
     * the mapper, which produced a fresh INSERT every time (no Long id was
     * propagated). This new version fixes the upgrade flow: the BillingController
     * subscription upgrade now actually mutates the existing row.
     */
    @Override
    @Transactional
    public Tenant save(Tenant tenant) {
        TenantEntity existing = jpa.findAll().stream()
                .filter(e -> e.getTenantId().equals(tenant.getId().value()))
                .findFirst()
                .orElse(null);
        if (existing == null) {
            TenantEntity entity = mapper.toEntity(tenant);
            return mapper.toDomain(jpa.save(entity));
        }
        existing.setBusinessName(tenant.getBusinessName());
        existing.setRuc(tenant.getRuc());
        existing.setPhone(tenant.getPhone());
        existing.setLogoUrl(tenant.getLogoUrl());
        existing.setPlan(tenant.getPlan());
        return mapper.toDomain(jpa.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> findById(TenantId id) {
        return jpa.findAll().stream()
                .filter(e -> e.getTenantId().equals(id.value()))
                .findFirst()
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(TenantId id) {
        return jpa.findAll().stream()
                .anyMatch(e -> e.getTenantId().equals(id.value()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> findAll() {
        return jpa.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}