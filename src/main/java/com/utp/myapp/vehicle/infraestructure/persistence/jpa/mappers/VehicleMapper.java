package com.utp.myapp.vehicle.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;
import com.utp.myapp.vehicle.domain.model.valueobjects.*;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities.VehicleEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class VehicleMapper {

    public Vehicle toDomain(VehicleEntity entity) {
        if (entity == null) return null;
        List<String> imageUrls = Collections.emptyList();
        if (entity.getImageUrls() != null && !entity.getImageUrls().isBlank()) {
            imageUrls = Arrays.asList(entity.getImageUrls().replaceAll("[\\[\\]\"]", "").split("\\s*,\\s*"));
        }
        return new Vehicle.Builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .make(entity.getMake())
                .model(entity.getModel())
                .year(entity.getYear())
                .plate(LicensePlate.of(entity.getPlate()))
                .color(entity.getColor())
                .vin(entity.getVin() != null ? VIN.of(entity.getVin()) : null)
                .mileage(new Mileage(entity.getMileageValue(),
                        entity.getMileageUnit() != null ? Mileage.MileageUnit.valueOf(entity.getMileageUnit()) : Mileage.MileageUnit.KM))
                .fuelType(entity.getFuelType())
                .imageUrls(imageUrls)
                .tenantId(entity.getTenantId())
                .build();
    }

    public VehicleEntity toEntity(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleEntity entity = new VehicleEntity();
        entity.setId(vehicle.getId());
        entity.setCustomerId(vehicle.getCustomerId());
        entity.setMake(vehicle.getMake());
        entity.setModel(vehicle.getModel());
        entity.setYear(vehicle.getYear());
        entity.setPlate(vehicle.getPlate() != null ? vehicle.getPlate().value() : null);
        entity.setColor(vehicle.getColor());
        entity.setVin(vehicle.getVin() != null ? vehicle.getVin().value() : null);
        entity.setMileageValue(vehicle.getMileage() != null ? vehicle.getMileage().value() : 0);
        entity.setMileageUnit(vehicle.getMileage() != null ? vehicle.getMileage().unit().name() : "KM");
        entity.setFuelType(vehicle.getFuelType());
        entity.setImageUrls(vehicle.getImageUrls() != null ? vehicle.getImageUrls().toString() : "[]");
        entity.setTenantId(vehicle.getTenantId());
        return entity;
    }
}
