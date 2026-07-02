package com.utp.myapp.catalog.domain.model.entities;

public class PartModelCompatibility {
    private Long id;
    private Long partCategoryId;
    private String vehicleMake;
    private String vehicleModel;
    private int yearFrom;
    private int yearTo;

    private PartModelCompatibility() {}

    public Long getId() { return id; }
    public Long getPartCategoryId() { return partCategoryId; }
    public String getVehicleMake() { return vehicleMake; }
    public String getVehicleModel() { return vehicleModel; }
    public int getYearFrom() { return yearFrom; }
    public int getYearTo() { return yearTo; }

    public static class Builder {
        private final PartModelCompatibility c = new PartModelCompatibility();
        public Builder id(Long id) { c.id = id; return this; }
        public Builder partCategoryId(Long id) { c.partCategoryId = id; return this; }
        public Builder vehicleMake(String m) { c.vehicleMake = m; return this; }
        public Builder vehicleModel(String m) { c.vehicleModel = m; return this; }
        public Builder yearFrom(int y) { c.yearFrom = y; return this; }
        public Builder yearTo(int y) { c.yearTo = y; return this; }
        public PartModelCompatibility build() { return c; }
    }
}
