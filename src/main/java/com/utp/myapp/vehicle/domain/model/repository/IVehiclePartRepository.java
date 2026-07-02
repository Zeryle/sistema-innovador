package com.utp.myapp.vehicle.domain.model.repository;

import com.utp.myapp.vehicle.domain.model.entities.VehiclePart;

import java.util.List;
import java.util.Optional;

public interface IVehiclePartRepository {
    VehiclePart save(VehiclePart part);
    Optional<VehiclePart> findById(Long id);
    List<VehiclePart> findByVehicleId(Long vehicleId);
    void deleteById(Long id);
}
