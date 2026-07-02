package com.utp.myapp.workorder.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.workorder.domain.model.valueobjects.WorkOrderStatus;
import com.utp.myapp.workorder.infraestructure.persistence.jpa.entities.WorkOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPAWorkOrderRepository extends JpaRepository<WorkOrderEntity, Long> {

    List<WorkOrderEntity> findByCustomerId(Integer customerId);

    List<WorkOrderEntity> findByVehicleId(Long vehicleId);

    List<WorkOrderEntity> findByStatus(WorkOrderStatus status);

    Page<WorkOrderEntity> findByTenantId(String tenantId, Pageable pageable);
}
