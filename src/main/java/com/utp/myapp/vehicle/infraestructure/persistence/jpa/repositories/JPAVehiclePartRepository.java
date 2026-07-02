package com.utp.myapp.vehicle.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities.VehiclePartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPAVehiclePartRepository extends JpaRepository<VehiclePartEntity, Long> {

    List<VehiclePartEntity> findByVehicleId(Long vehicleId);
}
