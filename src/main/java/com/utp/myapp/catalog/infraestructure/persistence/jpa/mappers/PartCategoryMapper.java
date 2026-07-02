package com.utp.myapp.catalog.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.catalog.domain.model.aggregates.PartCategory;
import com.utp.myapp.catalog.infraestructure.persistence.jpa.entities.PartCategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class PartCategoryMapper {

    public PartCategory toDomain(PartCategoryEntity entity) {
        if (entity == null) return null;
        return new PartCategory.Builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .parentCategoryId(entity.getParentCategoryId())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public PartCategoryEntity toEntity(PartCategory domain) {
        if (domain == null) return null;
        PartCategoryEntity entity = new PartCategoryEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setParentCategoryId(domain.getParentCategoryId());
        entity.setImageUrl(domain.getImageUrl());
        entity.setTenantId(domain.getTenantId());
        return entity;
    }
}
