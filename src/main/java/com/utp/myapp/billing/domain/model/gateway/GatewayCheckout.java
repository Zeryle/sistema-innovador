package com.utp.myapp.billing.domain.model.gateway;

import com.utp.myapp.billing.domain.model.valueobjects.PaymentProvider;

/**
 * Lightweight result of a successful {@link PaymentGateway#createCheckout} call.
 * Shapes mirror Stripe's "checkout.session.created" event payload, minus
 * fields we don't use.
 *
 *  - {@code url}             : where to redirect the user in production
 *  - {@code expiresAtEpoch}  : unix seconds (same as Stripe) so the frontend
 *                              can compare against Date.now()/1000 without
 *                              parsing ISO strings in two ways
 */
public record GatewayCheckout(
        String providerSessionId,        // cs_... / mock_cs_...
        PaymentProvider provider,         // "stripe" or "mock"
        String url,
        long expiresAtEpoch
) {}
