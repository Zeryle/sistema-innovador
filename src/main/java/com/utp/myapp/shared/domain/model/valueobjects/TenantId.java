package com.utp.myapp.shared.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record TenantId(String value) {

    public TenantId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TenantId cannot be null or empty");
        }
    }

    public static TenantId generate() {
        return new TenantId(UUID.randomUUID().toString());
    }

    public static TenantId of(String value) {
        return new TenantId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantId tenantId)) return false;
        return Objects.equals(value, tenantId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
