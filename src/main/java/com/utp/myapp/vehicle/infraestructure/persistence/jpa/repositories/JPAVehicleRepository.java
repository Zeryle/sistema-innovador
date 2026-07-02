package com.utp.myapp.vehicle.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JPAVehicleRepository extends JpaRepository<VehicleEntity, Long> {

    List<VehicleEntity> findByCustomerId(Integer customerId);

    Optional<VehicleEntity> findByPlate(String plate);

    Optional<VehicleEntity> findByVin(String vin);

    Page<VehicleEntity> findByTenantId(String tenantId, Pageable pageable);

    @Query("SELECT v FROM VehicleEntity v WHERE v.tenantId = :tenantId AND " +
            "(LOWER(v.make) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(v.model) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "v.plate LIKE CONCAT('%', :q, '%'))")
    Page<VehicleEntity> search(@Param("tenantId") String tenantId, @Param("q") String q, Pageable pageable);
}
