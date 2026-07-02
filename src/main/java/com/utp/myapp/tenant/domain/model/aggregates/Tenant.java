package com.utp.myapp.tenant.domain.model.aggregates;

import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;

import java.time.LocalDateTime;

/**
 * Tenant aggregate root — represents a single auto shop business.
 */
public class Tenant extends AuditableAggregateRoot {

    private TenantId id;
    private String businessName;
    private String ruc;
    private String phone;
    private String logoUrl;
    private SubscriptionPlan plan;

    private Tenant() {
    }

    public static Tenant create(TenantId id, String businessName, String phone, SubscriptionPlan plan) {
        Tenant tenant = new Tenant();
        tenant.id = id;
        tenant.businessName = businessName;
        tenant.phone = phone;
        tenant.plan = plan;
        tenant.createdAt = LocalDateTime.now();
        tenant.updatedAt = LocalDateTime.now();
        tenant.version = 0L;
        return tenant;
    }

    public void updateBusinessInfo(String businessName, String phone, String ruc) {
        this.businessName = businessName;
        this.phone = phone;
        this.ruc = ruc;
        markUpdated();
    }

    public void updatePlan(SubscriptionPlan newPlan) {
        this.plan = newPlan;
        markUpdated();
    }

    public void updateLogo(String logoUrl) {
        this.logoUrl = logoUrl;
        markUpdated();
    }

    public TenantId getId() {
        return id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRuc() {
        return ruc;
    }

    public String getPhone() {
        return phone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public static class Builder {
        private final Tenant tenant = new Tenant();

        public Builder id(TenantId id) {
            tenant.id = id;
            return this;
        }

        public Builder businessName(String businessName) {
            tenant.businessName = businessName;
            return this;
        }

        public Builder ruc(String ruc) {
            tenant.ruc = ruc;
            return this;
        }

        public Builder phone(String phone) {
            tenant.phone = phone;
            return this;
        }

        public Builder logoUrl(String logoUrl) {
            tenant.logoUrl = logoUrl;
            return this;
        }

        public Builder plan(SubscriptionPlan plan) {
            tenant.plan = plan;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            tenant.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            tenant.updatedAt = updatedAt;
            return this;
        }

        public Tenant build() {
            return tenant;
        }
    }
}
