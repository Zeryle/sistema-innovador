package com.utp.myapp.shared.infraestructure.config;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;

/**
 * ThreadLocal-based tenant context that stores the current tenant ID for the duration of a request.
 * Used by {@link TenantFilter} to set the context and {@link BaseEntity} lifecycle callbacks to auto-populate tenant_id.
 */
public final class TenantContext {

    private static final ThreadLocal<String> TENANT_HOLDER = new ThreadLocal<>();

    private TenantContext() {
        // Utility class
    }

    public static void setTenantId(String tenantId) {
        TENANT_HOLDER.set(tenantId);
        BaseEntity.TenantContextHolder.setTenantId(tenantId);
    }

    public static String getTenantId() {
        return TENANT_HOLDER.get();
    }

    public static void clear() {
        TENANT_HOLDER.remove();
        BaseEntity.TenantContextHolder.clear();
    }
}
