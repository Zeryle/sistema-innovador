package com.utp.myapp.workorder.domain.model.aggregates;

import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.utp.myapp.shared.domain.model.valueobjects.Money;
import com.utp.myapp.workorder.domain.model.entities.WorkOrderItem;
import com.utp.myapp.workorder.domain.model.valueobjects.Priority;
import com.utp.myapp.workorder.domain.model.valueobjects.WorkOrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkOrder extends AuditableAggregateRoot {

    private Long id;
    private Long vehicleId;
    private Integer customerId;
    private Long mechanicId;
    private String tenantId;
    private WorkOrderStatus status;
    private String description;
    private String diagnosticNotes;
    private Money estimatedCost;
    private Money finalCost;
    private Priority priority;
    private LocalDateTime startDate;
    private LocalDateTime estimatedEndDate;
    private LocalDateTime completedDate;
    private List<WorkOrderItem> items;

    private WorkOrder() { this.items = new ArrayList<>(); }

    public Long getId() { return id; }
    public Long getVehicleId() { return vehicleId; }
    public Integer getCustomerId() { return customerId; }
    public Long getMechanicId() { return mechanicId; }
    public String getTenantId() { return tenantId; }
    public WorkOrderStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public String getDiagnosticNotes() { return diagnosticNotes; }
    public Money getEstimatedCost() { return estimatedCost; }
    public Money getFinalCost() { return finalCost; }
    public Priority getPriority() { return priority; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEstimatedEndDate() { return estimatedEndDate; }
    public LocalDateTime getCompletedDate() { return completedDate; }
    public List<WorkOrderItem> getItems() { return List.copyOf(items); }

    public void transitionTo(WorkOrderStatus newStatus) {
        this.status = newStatus;
        if (newStatus == WorkOrderStatus.COMPLETED) {
            this.completedDate = LocalDateTime.now();
        }
        markUpdated();
    }

    public void addItem(WorkOrderItem item) {
        this.items.add(item);
        markUpdated();
    }

    public static class Builder {
        private final WorkOrder wo = new WorkOrder();
        public Builder id(Long id) { wo.id = id; return this; }
        public Builder vehicleId(Long id) { wo.vehicleId = id; return this; }
        public Builder customerId(Integer id) { wo.customerId = id; return this; }
        public Builder mechanicId(Long id) { wo.mechanicId = id; return this; }
        public Builder tenantId(String id) { wo.tenantId = id; return this; }
        public Builder status(WorkOrderStatus s) { wo.status = s; return this; }
        public Builder description(String d) { wo.description = d; return this; }
        public Builder estimatedCost(Money c) { wo.estimatedCost = c; return this; }
        public Builder priority(Priority p) { wo.priority = p; return this; }
        public Builder startDate(LocalDateTime d) { wo.startDate = d; return this; }
        public WorkOrder build() { return wo; }
    }
}
