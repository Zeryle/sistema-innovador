package com.utp.myapp.billing.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import com.utp.myapp.billing.domain.model.valueobjects.PaymentProvider;
import com.utp.myapp.billing.infraestructure.persistence.jpa.entities.CheckoutSessionEntity;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CheckoutSessionMapper {

    public CheckoutSession toDomain(CheckoutSessionEntity e) {
        if (e == null) return null;
        TenantId tenantId = TenantId.of(e.getTenantId());
        SubscriptionPlan plan = SubscriptionPlan.valueOf(e.getTargetPlan());
        CheckoutSession s = CheckoutSession.open(
                tenantId, plan, e.getExpectedAmount(), e.getCurrency(),
                e.getIdempotencyKey());
        s.assignId(e.getId());
        if (e.getProviderName() != null && e.getProviderIntentId() != null) {
            s.markProcessing(new PaymentProvider(e.getProviderName(), e.getProviderIntentId()));
        }
        switch (e.getStatus()) {
            case SUCCEEDED -> s.markSucceeded();
            case CANCELLED -> s.markCancelled();
            // OPEN, PROCESSING, EXPIRED = no-op from open
        }
        return s;
    }

    public CheckoutSessionEntity toEntity(CheckoutSession s) {
        CheckoutSessionEntity e = new CheckoutSessionEntity();
        e.setId(s.id());
        e.setTenantId(s.tenantId().value());
        e.setTargetPlan(s.targetPlan().name());
        e.setExpectedAmount(s.expectedAmount());
        e.setCurrency(s.currency());
        e.setStatus(s.status());
        e.setIdempotencyKey(s.idempotencyKey());
        if (s.paymentIntent() != null) {
            e.setProviderName(s.paymentIntent().provider());
            e.setProviderIntentId(s.paymentIntent().intentId());
        }
        e.setSucceededAt(s.succeededAt());
        e.setCancelledAt(s.cancelledAt());
        e.setCreatedAt(s.getCreatedAt());
        e.setUpdatedAt(s.getUpdatedAt());
        return e;
    }

    /** Helper to update the status of an existing entity (used by the webhook handler). */
    public void applyStatus(CheckoutSessionEntity e, CheckoutSession aggregate) {
        e.setStatus(aggregate.status());
        if (aggregate.paymentIntent() != null) {
            e.setProviderName(aggregate.paymentIntent().provider());
            e.setProviderIntentId(aggregate.paymentIntent().intentId());
        }
        e.setSucceededAt(aggregate.succeededAt());
        e.setCancelledAt(aggregate.cancelledAt());
        e.setUpdatedAt(aggregate.getUpdatedAt());
    }
}
