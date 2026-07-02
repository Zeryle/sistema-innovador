package com.utp.myapp.workorder.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.workorder.domain.model.aggregates.WorkOrder;
import com.utp.myapp.workorder.domain.model.repository.IWorkOrderRepository;
import com.utp.myapp.workorder.domain.model.valueobjects.WorkOrderStatus;
import com.utp.myapp.workorder.infraestructure.persistence.jpa.entities.WorkOrderEntity;
import com.utp.myapp.workorder.infraestructure.persistence.jpa.mappers.WorkOrderMapper;
import com.utp.myapp.workorder.infraestructure.persistence.jpa.repositories.JPAWorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WorkOrderRepositoryAdapter implements IWorkOrderRepository {

    private final JPAWorkOrderRepository jpa;
    private final WorkOrderMapper mapper;

    @Override
    public WorkOrder save(WorkOrder workOrder) {
        WorkOrderEntity entity = mapper.toEntity(workOrder);
        WorkOrderEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<WorkOrder> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<WorkOrder> findByCustomerId(Integer customerId) {
        return jpa.findByCustomerId(customerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WorkOrder> findByVehicleId(Long vehicleId) {
        return jpa.findByVehicleId(vehicleId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        return jpa.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WorkOrder> findByTenantId(String tenantId, int page, int size) {
        return jpa.findByTenantId(tenantId, PageRequest.of(page, size))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
