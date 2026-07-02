package com.utp.myapp.catalog.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.catalog.infraestructure.persistence.jpa.entities.PartCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JPAPartCategoryRepository extends JpaRepository<PartCategoryEntity, Long> {

    List<PartCategoryEntity> findByParentCategoryId(Long parentId);

    @Query("SELECT p FROM PartCategoryEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<PartCategoryEntity> searchByName(@Param("q") String query);
}
