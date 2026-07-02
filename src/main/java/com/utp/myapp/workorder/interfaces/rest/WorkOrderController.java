package com.utp.myapp.workorder.interfaces.rest;

import com.utp.myapp.sales.domain.model.aggregates.Customer;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import com.utp.myapp.shared.domain.model.valueobjects.Money;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.vehicle.domain.model.aggregates.Vehicle;
import com.utp.myapp.vehicle.domain.model.repository.IVehicleRepository;
import com.utp.myapp.workorder.domain.model.aggregates.WorkOrder;
import com.utp.myapp.workorder.domain.model.repository.IWorkOrderRepository;
import com.utp.myapp.workorder.domain.model.valueobjects.WorkOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final IWorkOrderRepository repository;
    private final IVehicleRepository vehicleRepository;
    private final ICustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) String status) {
        String tenantId = TenantContext.getTenantId();
        List<WorkOrder> orders;
        if (status != null && !status.isBlank()) {
            orders = repository.findByStatus(WorkOrderStatus.valueOf(status));
        } else {
            orders = repository.findByTenantId(tenantId, 0, 50);
        }
        List<Map<String, Object>> result = orders.stream().map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(wo -> ResponseEntity.ok(ApiResponse.ok(toMap(wo))))
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder", id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> body) {
        String tenantId = TenantContext.getTenantId();
        WorkOrder.Builder builder = new WorkOrder.Builder()
                .vehicleId(body.get("vehicleId") != null ? ((Number) body.get("vehicleId")).longValue() : null)
                .customerId(body.get("customerId") != null ? ((Number) body.get("customerId")).intValue() : null)
                .tenantId(tenantId)
                .description((String) body.get("description"))
                .status(WorkOrderStatus.RECEIVED);
        if (body.get("estimatedCost") != null) {
            builder.estimatedCost(Money.of(((Number) body.get("estimatedCost")).doubleValue()));
        }
        WorkOrder wo = builder.build();
        WorkOrder saved = repository.save(wo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toMap(saved)));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getByVehicle(@PathVariable Long vehicleId) {
        List<Map<String, Object>> result = repository.findByVehicleId(vehicleId).stream()
                .map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    private Map<String, Object> toMap(WorkOrder wo) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", wo.getId());
        m.put("vehicleId", wo.getVehicleId());
        m.put("customerId", wo.getCustomerId());

        // Enrich with names
        if (wo.getVehicleId() != null) {
            vehicleRepository.findById(wo.getVehicleId()).ifPresent(v ->
                m.put("vehiclePlate", v.getPlate() != null ? v.getPlate().value() : "")
            );
        }
        if (wo.getCustomerId() != null) {
            Customer c = customerRepository.listById(wo.getCustomerId());
            if (c != null) m.put("customerName", c.getFullName());
        }

        m.put("status", wo.getStatus() != null ? wo.getStatus().name() : null);
        m.put("description", wo.getDescription());
        m.put("diagnosticNotes", wo.getDiagnosticNotes());
        m.put("estimatedCost", wo.getEstimatedCost() != null ? wo.getEstimatedCost().amount().doubleValue() : 0);
        m.put("finalCost", wo.getFinalCost() != null ? wo.getFinalCost().amount().doubleValue() : 0);
        m.put("priority", wo.getPriority() != null ? wo.getPriority().name() : null);
        m.put("startDate", wo.getStartDate() != null ? wo.getStartDate().toString() : null);
        m.put("completedDate", wo.getCompletedDate() != null ? wo.getCompletedDate().toString() : null);
        m.put("createdAt", wo.getCreatedAt() != null ? wo.getCreatedAt().toString() : null);
        return m;
    }
}
