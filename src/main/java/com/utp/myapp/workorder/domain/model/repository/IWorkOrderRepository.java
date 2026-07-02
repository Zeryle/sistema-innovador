package com.utp.myapp.workorder.domain.model.repository;

import com.utp.myapp.workorder.domain.model.aggregates.WorkOrder;
import com.utp.myapp.workorder.domain.model.valueobjects.WorkOrderStatus;

import java.util.List;
import java.util.Optional;

public interface IWorkOrderRepository {
    WorkOrder save(WorkOrder workOrder);
    Optional<WorkOrder> findById(Long id);
    List<WorkOrder> findByCustomerId(Integer customerId);
    List<WorkOrder> findByVehicleId(Long vehicleId);
    List<WorkOrder> findByStatus(WorkOrderStatus status);
    List<WorkOrder> findByTenantId(String tenantId, int page, int size);
    void deleteById(Long id);
}
