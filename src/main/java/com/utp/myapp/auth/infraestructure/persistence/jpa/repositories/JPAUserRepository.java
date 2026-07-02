package com.utp.myapp.auth.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.auth.infraestructure.persistence.jpa.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JPAUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId")
    List<UserEntity> findByTenantId(@Param("tenantId") String tenantId);
}
