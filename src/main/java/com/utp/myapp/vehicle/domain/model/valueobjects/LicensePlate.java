package com.utp.myapp.vehicle.domain.model.valueobjects;

public record LicensePlate(String value) {

    public LicensePlate {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty");
        }
        String sanitized = value.toUpperCase().replaceAll("[\\s-]", "");
        if (sanitized.length() < 5 || sanitized.length() > 8) {
            throw new IllegalArgumentException("Invalid license plate format: " + value);
        }
    }

    public static LicensePlate of(String value) {
        return new LicensePlate(value);
    }

    public String normalized() {
        return value.toUpperCase().replaceAll("[\\s-]", "");
    }

    @Override
    public String toString() {
        return value.toUpperCase();
    }
}
