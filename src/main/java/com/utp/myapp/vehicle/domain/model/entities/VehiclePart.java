package com.utp.myapp.vehicle.domain.model.entities;

import java.time.LocalDate;

public class VehiclePart {

    private Long id;
    private Long vehicleId;
    private Long partCategoryId;
    private String name;
    private String condition;
    private LocalDate lastInspectionDate;
    private String imageUrl;
    private String notes;

    private VehiclePart() {}

    public Long getId() { return id; }
    public Long getVehicleId() { return vehicleId; }
    public Long getPartCategoryId() { return partCategoryId; }
    public String getName() { return name; }
    public String getCondition() { return condition; }
    public LocalDate getLastInspectionDate() { return lastInspectionDate; }
    public String getImageUrl() { return imageUrl; }
    public String getNotes() { return notes; }

    public void updateCondition(String condition, String notes) {
        this.condition = condition;
        this.notes = notes;
        this.lastInspectionDate = LocalDate.now();
    }

    public static class Builder {
        private final VehiclePart part = new VehiclePart();

        public Builder id(Long id) { part.id = id; return this; }
        public Builder vehicleId(Long vehicleId) { part.vehicleId = vehicleId; return this; }
        public Builder partCategoryId(Long partCategoryId) { part.partCategoryId = partCategoryId; return this; }
        public Builder name(String name) { part.name = name; return this; }
        public Builder condition(String condition) { part.condition = condition; return this; }
        public Builder lastInspectionDate(LocalDate lastInspectionDate) { part.lastInspectionDate = lastInspectionDate; return this; }
        public Builder imageUrl(String imageUrl) { part.imageUrl = imageUrl; return this; }
        public Builder notes(String notes) { part.notes = notes; return this; }

        public VehiclePart build() { return part; }
    }
}
