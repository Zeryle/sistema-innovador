package com.utp.myapp.billing.application.command;

import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Command: tenant wants to start an upgrade flow to a new plan.
 * The handler creates a CheckoutSession aggregate, persists it,
 * calls the (mock or real) Stripe gateway, and returns the redirect URL.
 */
@Getter
@RequiredArgsConstructor
@Builder
public class StartUpgradeCheckoutCommand {
    /** Plan the tenant is upgrading to. Cannot be the same as the current plan. */
    private final SubscriptionPlan targetPlan;
    /** Optional idempotency key from the client. If null, one is generated. */
    private final String idempotencyKey;
    /** Where to redirect after a successful payment. */
    private final String successUrl;
    /** Where to redirect after a cancelled payment. */
    private final String cancelUrl;
}
