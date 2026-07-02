package com.utp.myapp.tenant.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantEntity extends BaseEntity {

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 20)
    private SubscriptionPlan plan;

    @PrePersist
    @PreUpdate
    void setTenantForSelf() {
        // The tenant entity itself has tenant_id = the tenant's own ID (for multi-tenancy filtering)
        // This is set explicitly when creating the tenant
    }
}
