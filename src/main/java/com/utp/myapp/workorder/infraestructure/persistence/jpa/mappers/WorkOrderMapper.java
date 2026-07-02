package com.utp.myapp.workorder.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.shared.domain.model.valueobjects.Money;
import com.utp.myapp.workorder.domain.model.aggregates.WorkOrder;
import com.utp.myapp.workorder.infraestructure.persistence.jpa.entities.WorkOrderEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WorkOrderMapper {

    public WorkOrder toDomain(WorkOrderEntity entity) {
        if (entity == null) return null;
        return new WorkOrder.Builder()
                .id(entity.getId())
                .vehicleId(entity.getVehicleId())
                .customerId(entity.getCustomerId())
                .mechanicId(entity.getMechanicId())
                .tenantId(entity.getTenantId())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .estimatedCost(entity.getEstimatedCost() != null ? Money.of(entity.getEstimatedCost()) : null)
                .priority(entity.getPriority())
                .startDate(entity.getStartDate())
                .build();
    }

    public WorkOrderEntity toEntity(WorkOrder domain) {
        if (domain == null) return null;
        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(domain.getId());
        entity.setVehicleId(domain.getVehicleId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setMechanicId(domain.getMechanicId());
        entity.setTenantId(domain.getTenantId());
        entity.setStatus(domain.getStatus());
        entity.setDescription(domain.getDescription());
        entity.setDiagnosticNotes(domain.getDiagnosticNotes());
        entity.setEstimatedCost(domain.getEstimatedCost() != null ? domain.getEstimatedCost().amount() : null);
        entity.setFinalCost(domain.getFinalCost() != null ? domain.getFinalCost().amount() : null);
        entity.setPriority(domain.getPriority());
        entity.setStartDate(domain.getStartDate());
        entity.setEstimatedEndDate(domain.getEstimatedEndDate());
        entity.setCompletedDate(domain.getCompletedDate());
        return entity;
    }
}
