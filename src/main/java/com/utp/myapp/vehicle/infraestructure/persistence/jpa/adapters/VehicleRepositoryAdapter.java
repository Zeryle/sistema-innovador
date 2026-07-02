package com.utp.myapp.vehicle.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;
import com.utp.myapp.vehicle.domain.model.repository.IVehicleRepository;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities.VehicleEntity;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.mappers.VehicleMapper;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.repositories.JPAVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class VehicleRepositoryAdapter implements IVehicleRepository {

    private final JPAVehicleRepository jpa;
    private final VehicleMapper mapper;

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = mapper.toEntity(vehicle);
        VehicleEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Vehicle> findByCustomerId(Integer customerId) {
        return jpa.findByCustomerId(customerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return jpa.findByPlate(plate).map(mapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findByVin(String vin) {
        return jpa.findByVin(vin).map(mapper::toDomain);
    }

    @Override
    public List<Vehicle> findByTenantId(String tenantId, int page, int size) {
        return jpa.findByTenantId(tenantId, PageRequest.of(page, size))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Vehicle> searchByTenant(String tenantId, String query, int page, int size) {
        return jpa.search(tenantId, query, PageRequest.of(page, size))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
