
package com.utp.myapp.billing.domain.model.aggregates;

import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import com.utp.myapp.billing.domain.model.valueobjects.PaymentProvider;
import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * One attempt by a tenant to upgrade their subscription plan.
 *
 * Aggregates the data needed to:
 *  - redirect the user to the payment UI (URL, plan, price)
 *  - match the incoming webhook to the originating tenant (idempotencyKey)
 *  - transition through {@link CheckoutStatus} states safely
 *
 * Invariants enforced via markSucceeded / markCancelled / markProcessing:
 *  - Cannot mark SUCCEEDED twice
 *  - Cannot mark CANCELLED after SUCCEEDED
 *  - "succeededAt" is set only when the webhook confirms the charge
 *
 * On SUCCEEDED the price + currency are frozen (charge happened), and the
 * target plan is what the tenant now has.
 */
public class CheckoutSession extends AuditableAggregateRoot {

    private String id;                          // mock_cs_<uuid>  (same shape as Stripe `cs_...`)
    private TenantId tenantId;
    private SubscriptionPlan targetPlan;
    private BigDecimal expectedAmount;
    private String currency;
    private CheckoutStatus status;
    private PaymentProvider paymentIntent;      // null until SUCCEEDED
    private String idempotencyKey;              // client-supplied (or generated) for webhook dedup
    private LocalDateTime succeededAt;
    private LocalDateTime cancelledAt;

    private CheckoutSession() {}

    public static CheckoutSession open(TenantId tenantId, SubscriptionPlan plan,
                                      BigDecimal expectedAmount, String currency,
                                      String idempotencyKey) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(plan, "plan");
        CheckoutSession s = new CheckoutSession();
        s.tenantId = tenantId;
        s.targetPlan = plan;
        s.expectedAmount = expectedAmount;
        s.currency = currency;
        s.status = CheckoutStatus.OPEN;
        s.idempotencyKey = idempotencyKey != null ? idempotencyKey
                : java.util.UUID.randomUUID().toString();
        s.createdAt = LocalDateTime.now();
        return s;
    }

    public void assignId(String id) { this.id = id; }

    public void markProcessing(PaymentProvider provider) {
        if (status != CheckoutStatus.OPEN) {
            throw new IllegalStateException("Cannot mark PROCESSING from " + status);
        }
        this.paymentIntent = provider;
        this.status = CheckoutStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void markSucceeded() {
        if (status == CheckoutStatus.SUCCEEDED) return;     // idempotent
        if (status != CheckoutStatus.PROCESSING) {
            throw new IllegalStateException("Cannot mark SUCCEEDED from " + status);
        }
        this.status = CheckoutStatus.SUCCEEDED;
        this.succeededAt = LocalDateTime.now();
        this.updatedAt = this.succeededAt;
    }

    public void markCancelled() {
        if (status == CheckoutStatus.SUCCEEDED) {
            throw new IllegalStateException("Cannot cancel a SUCCEEDED checkout");
        }
        this.status = CheckoutStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = this.cancelledAt;
    }

    public boolean isFinal() {
        return status == CheckoutStatus.SUCCEEDED
                || status == CheckoutStatus.CANCELLED
                || status == CheckoutStatus.EXPIRED;
    }

    public String id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public SubscriptionPlan targetPlan() { return targetPlan; }
    public BigDecimal expectedAmount() { return expectedAmount; }
    public String currency() { return currency; }
    public CheckoutStatus status() { return status; }
    public PaymentProvider paymentIntent() { return paymentIntent; }
    public String idempotencyKey() { return idempotencyKey; }
    public LocalDateTime succeededAt() { return succeededAt; }
    public LocalDateTime cancelledAt() { return cancelledAt; }
}
