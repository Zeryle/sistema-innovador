package com.utp.myapp.vehicle.domain.model.valueobjects;

public record VIN(String value) {

    public VIN {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("VIN cannot be null or empty");
        }
        String sanitized = value.toUpperCase().replaceAll("[IOQ]", "");
        if (sanitized.length() != 17) {
            throw new IllegalArgumentException("VIN must be exactly 17 characters");
        }
    }

    public static VIN of(String value) {
        return new VIN(value);
    }

    @Override
    public String toString() {
        return value.toUpperCase();
    }
}
