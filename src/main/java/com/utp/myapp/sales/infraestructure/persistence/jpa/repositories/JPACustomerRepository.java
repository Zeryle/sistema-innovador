package com.utp.myapp.sales.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.sales.infraestructure.persistence.jpa.entities.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JPACustomerRepository extends JpaRepository<CustomerEntity, Integer> {

    Optional<CustomerEntity> findByDni(String dni);

    Optional<CustomerEntity> findByPhone(String phone);

    Optional<CustomerEntity> findByEmail(String email);

    @Query("SELECT c FROM CustomerEntity c WHERE c.tenantId = :tenantId")
    Page<CustomerEntity> findByTenantId(@Param("tenantId") String tenantId, Pageable pageable);

    @Query("SELECT c FROM CustomerEntity c WHERE c.tenantId = :tenantId AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "c.dni LIKE CONCAT('%', :query, '%') OR " +
            "c.phone LIKE CONCAT('%', :query, '%'))")
    Page<CustomerEntity> searchByTenant(@Param("tenantId") String tenantId,
                                         @Param("query") String query,
                                         Pageable pageable);
}
