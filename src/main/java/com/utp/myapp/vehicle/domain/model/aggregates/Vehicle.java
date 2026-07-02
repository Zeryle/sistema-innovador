package com.utp.myapp.vehicle.domain.model.aggregates;

import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.utp.myapp.vehicle.domain.model.valueobjects.FuelType;
import com.utp.myapp.vehicle.domain.model.valueobjects.LicensePlate;
import com.utp.myapp.vehicle.domain.model.valueobjects.Mileage;
import com.utp.myapp.vehicle.domain.model.valueobjects.VIN;

import java.util.ArrayList;
import java.util.List;

public class Vehicle extends AuditableAggregateRoot {

    private Long id;
    private Integer customerId;
    private String make;
    private String model;
    private int year;
    private LicensePlate plate;
    private String color;
    private VIN vin;
    private Mileage mileage;
    private FuelType fuelType;
    private List<String> imageUrls;
    private String tenantId;

    private Vehicle() {
        this.imageUrls = new ArrayList<>();
    }

    public Long getId() { return id; }
    public Integer getCustomerId() { return customerId; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public LicensePlate getPlate() { return plate; }
    public String getColor() { return color; }
    public VIN getVin() { return vin; }
    public Mileage getMileage() { return mileage; }
    public FuelType getFuelType() { return fuelType; }
    public List<String> getImageUrls() { return List.copyOf(imageUrls); }
    public String getTenantId() { return tenantId; }

    public void updateMileage(Mileage newMileage) {
        if (newMileage.value() < this.mileage.value()) {
            throw new IllegalArgumentException("New mileage cannot be less than current mileage");
        }
        this.mileage = newMileage;
        markUpdated();
    }

    public void addImageUrl(String url) {
        this.imageUrls.add(url);
        markUpdated();
    }

    public void removeImageUrl(String url) {
        this.imageUrls.remove(url);
        markUpdated();
    }

    public void update(String make, String model, int year, String color,
                       FuelType fuelType, LicensePlate plate) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.fuelType = fuelType;
        this.plate = plate;
        markUpdated();
    }

    public static class Builder {
        private final Vehicle vehicle = new Vehicle();

        public Builder id(Long id) { vehicle.id = id; return this; }
        public Builder customerId(Integer customerId) { vehicle.customerId = customerId; return this; }
        public Builder make(String make) { vehicle.make = make; return this; }
        public Builder model(String model) { vehicle.model = model; return this; }
        public Builder year(int year) { vehicle.year = year; return this; }
        public Builder plate(LicensePlate plate) { vehicle.plate = plate; return this; }
        public Builder color(String color) { vehicle.color = color; return this; }
        public Builder vin(VIN vin) { vehicle.vin = vin; return this; }
        public Builder mileage(Mileage mileage) { vehicle.mileage = mileage; return this; }
        public Builder fuelType(FuelType fuelType) { vehicle.fuelType = fuelType; return this; }
        public Builder imageUrls(List<String> imageUrls) { vehicle.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>(); return this; }
        public Builder tenantId(String tenantId) { vehicle.tenantId = tenantId; return this; }

        public Vehicle build() { return vehicle; }
    }
}
