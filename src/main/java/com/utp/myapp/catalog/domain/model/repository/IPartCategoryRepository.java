package com.utp.myapp.catalog.domain.model.repository;

import com.utp.myapp.catalog.domain.model.aggregates.PartCategory;
import java.util.List;
import java.util.Optional;

public interface IPartCategoryRepository {
    PartCategory save(PartCategory category);
    Optional<PartCategory> findById(Long id);
    List<PartCategory> findAll();
    List<PartCategory> findByParentId(Long parentId);
    List<PartCategory> searchByName(String query);
    void deleteById(Long id);
}
