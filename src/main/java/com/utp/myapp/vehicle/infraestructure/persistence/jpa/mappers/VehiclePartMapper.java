package com.utp.myapp.vehicle.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.vehicle.domain.model.entities.VehiclePart;
import com.utp.myapp.vehicle.infraestructure.persistence.jpa.entities.VehiclePartEntity;
import org.springframework.stereotype.Component;

@Component
public class VehiclePartMapper {

    public VehiclePart toDomain(VehiclePartEntity entity) {
        if (entity == null) return null;
        return new VehiclePart.Builder()
                .id(entity.getId())
                .vehicleId(entity.getVehicleId())
                .partCategoryId(entity.getPartCategoryId())
                .name(entity.getName())
                .condition(entity.getConditionDescription())
                .lastInspectionDate(entity.getLastInspectionDate())
                .imageUrl(entity.getImageUrl())
                .notes(entity.getNotes())
                .build();
    }

    public VehiclePartEntity toEntity(VehiclePart domain) {
        if (domain == null) return null;
        VehiclePartEntity entity = new VehiclePartEntity();
        entity.setId(domain.getId());
        entity.setVehicleId(domain.getVehicleId());
        entity.setPartCategoryId(domain.getPartCategoryId());
        entity.setName(domain.getName());
        entity.setConditionDescription(domain.getCondition());
        entity.setLastInspectionDate(domain.getLastInspectionDate());
        entity.setImageUrl(domain.getImageUrl());
        entity.setNotes(domain.getNotes());
        return entity;
    }
}
