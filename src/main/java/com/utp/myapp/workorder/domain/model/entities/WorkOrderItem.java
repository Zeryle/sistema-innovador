package com.utp.myapp.workorder.domain.model.entities;

import com.utp.myapp.shared.domain.model.valueobjects.Money;
import com.utp.myapp.workorder.domain.model.valueobjects.RepairAction;

public class WorkOrderItem {
    private Long id;
    private Long workOrderId;
    private Long partCategoryId;
    private String partName;
    private int quantity;
    private Money unitCost;
    private Money laborCost;
    private RepairAction action;
    private String status;
    private String notes;

    private WorkOrderItem() {}

    public Long getId() { return id; }
    public Long getWorkOrderId() { return workOrderId; }
    public Long getPartCategoryId() { return partCategoryId; }
    public String getPartName() { return partName; }
    public int getQuantity() { return quantity; }
    public Money getUnitCost() { return unitCost; }
    public Money getLaborCost() { return laborCost; }
    public RepairAction getAction() { return action; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public static class Builder {
        private final WorkOrderItem item = new WorkOrderItem();
        public Builder id(Long id) { item.id = id; return this; }
        public Builder workOrderId(Long id) { item.workOrderId = id; return this; }
        public Builder partCategoryId(Long id) { item.partCategoryId = id; return this; }
        public Builder partName(String n) { item.partName = n; return this; }
        public Builder quantity(int q) { item.quantity = q; return this; }
        public Builder unitCost(Money c) { item.unitCost = c; return this; }
        public Builder laborCost(Money c) { item.laborCost = c; return this; }
        public Builder action(RepairAction a) { item.action = a; return this; }
        public Builder notes(String n) { item.notes = n; return this; }
        public WorkOrderItem build() { return item; }
    }
}
