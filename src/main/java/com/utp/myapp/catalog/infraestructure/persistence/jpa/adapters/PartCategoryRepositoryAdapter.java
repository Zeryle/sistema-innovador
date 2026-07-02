package com.utp.myapp.catalog.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.catalog.domain.model.aggregates.PartCategory;
import com.utp.myapp.catalog.domain.model.repository.IPartCategoryRepository;
import com.utp.myapp.catalog.infraestructure.persistence.jpa.entities.PartCategoryEntity;
import com.utp.myapp.catalog.infraestructure.persistence.jpa.mappers.PartCategoryMapper;
import com.utp.myapp.catalog.infraestructure.persistence.jpa.repositories.JPAPartCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PartCategoryRepositoryAdapter implements IPartCategoryRepository {

    private final JPAPartCategoryRepository jpa;
    private final PartCategoryMapper mapper;

    @Override
    public PartCategory save(PartCategory category) {
        PartCategoryEntity entity = mapper.toEntity(category);
        PartCategoryEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PartCategory> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PartCategory> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PartCategory> findByParentId(Long parentId) {
        return jpa.findByParentCategoryId(parentId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PartCategory> searchByName(String query) {
        return jpa.searchByName(query).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
