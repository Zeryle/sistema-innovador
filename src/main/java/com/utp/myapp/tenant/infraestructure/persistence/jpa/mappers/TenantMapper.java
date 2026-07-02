package com.utp.myapp.tenant.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.infraestructure.persistence.jpa.entities.TenantEntity;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public Tenant toDomain(TenantEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Tenant.Builder()
                .id(TenantId.of(entity.getTenantId()))
                .businessName(entity.getBusinessName())
                .ruc(entity.getRuc())
                .phone(entity.getPhone())
                .logoUrl(entity.getLogoUrl())
                .plan(entity.getPlan())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public TenantEntity toEntity(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        TenantEntity entity = new TenantEntity();
        entity.setTenantId(tenant.getId().value());
        entity.setBusinessName(tenant.getBusinessName());
        entity.setRuc(tenant.getRuc());
        entity.setPhone(tenant.getPhone());
        entity.setLogoUrl(tenant.getLogoUrl());
        entity.setPlan(tenant.getPlan());
        entity.setCreatedAt(tenant.getCreatedAt());
        entity.setUpdatedAt(tenant.getUpdatedAt());
        return entity;
    }
}
