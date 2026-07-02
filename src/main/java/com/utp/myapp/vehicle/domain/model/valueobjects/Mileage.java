package com.utp.myapp.vehicle.domain.model.valueobjects;

public record Mileage(int value, MileageUnit unit) {

    public enum MileageUnit { KM, MI }

    public Mileage {
        if (value < 0) {
            throw new IllegalArgumentException("Mileage cannot be negative");
        }
        if (unit == null) {
            unit = MileageUnit.KM;
        }
    }

    public static Mileage ofKm(int value) {
        return new Mileage(value, MileageUnit.KM);
    }

    public static Mileage ofMi(int value) {
        return new Mileage(value, MileageUnit.MI);
    }

    @Override
    public String toString() {
        return value + " " + unit.name().toLowerCase();
    }
}
