package com.utp.myapp.vehicle.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.vehicle.domain.model.entities.VehiclePart;
import com.utp.myapp.vehicle.domain.model.repository.IVehiclePartRepository;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities.VehiclePartEntity;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.mappers.VehiclePartMapper;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.repositories.JPAVehiclePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VehiclePartRepositoryAdapter implements IVehiclePartRepository {

    private final JPAVehiclePartRepository jpa;
    private final VehiclePartMapper mapper;

    @Override
    public VehiclePart save(VehiclePart part) {
        VehiclePartEntity entity = mapper.toEntity(part);
        VehiclePartEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<VehiclePart> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<VehiclePart> findByVehicleId(Long vehicleId) {
        return jpa.findByVehicleId(vehicleId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
