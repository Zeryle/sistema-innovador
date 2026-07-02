package com.utp.myapp.shared.infraestructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Base JPA entity with tenant discriminator and audit fields.
 * All JPA entities should extend this class to inherit multi-tenancy and auditing.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.tenantId == null) {
            this.tenantId = TenantContextHolder.getTenantId();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.tenantId == null) {
            this.tenantId = TenantContextHolder.getTenantId();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * ThreadLocal holder for tenant context. Used by @PrePersist/@PreUpdate callbacks.
     * Public so that {@link com.utp.myapp.shared.infraestructure.config.TenantContext} can sync with it.
     */
    public static class TenantContextHolder {
        private static final ThreadLocal<String> tenantHolder = new ThreadLocal<>();

        public static String getTenantId() {
            return tenantHolder.get();
        }

        public static void setTenantId(String tenantId) {
            tenantHolder.set(tenantId);
        }

        public static void clear() {
            tenantHolder.remove();
        }
    }
}
