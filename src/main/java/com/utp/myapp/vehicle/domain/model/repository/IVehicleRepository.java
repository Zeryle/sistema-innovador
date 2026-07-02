package com.utp.myapp.vehicle.domain.model.repository;

import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;

import java.util.List;
import java.util.Optional;

public interface IVehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Long id);
    List<Vehicle> findByCustomerId(Integer customerId);
    Optional<Vehicle> findByPlate(String plate);
    Optional<Vehicle> findByVin(String vin);
    List<Vehicle> findByTenantId(String tenantId, int page, int size);
    List<Vehicle> searchByTenant(String tenantId, String query, int page, int size);
    void deleteById(Long id);
}
