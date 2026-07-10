package com.utp.myapp.billing.domain.model.gateway;

import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.valueobjects.PaymentProvider;

import java.util.Map;

/**
 * Port to the external payment provider (Stripe in production, Mock in dev/demo).
 *
 * The mock implementation faithfully reproduces Stripe's API surface for the
 * CheckoutSession flows we use, so swapping it for a real {@code StripeGateway}
 * is a single Spring bean swap.
 *
 * Both implementations:
 *  - generate an opaque session id (cs_... in Stripe, mock_cs_... here)
 *  - accept the same input (expected price, target plan, return URL)
 *  - return the same DTO (id, url or token, expires_at)
 */
public interface PaymentGateway {

    /**
     * Creates a hosted checkout session for the given aggregate and returns the
     * metadata needed by the frontend to redirect the user (typically a URL).
     */
    GatewayCheckout createCheckout(CheckoutSession session, String successUrl, String cancelUrl);

    /**
     * Verifies a webhook payload came from the provider. In a real Stripe
     * integration this checks an HMAC signature. In the mock we just accept
     * the body — sufficient for the demo, distinct enough to make the swap
     * to the real Stripe adapter an obvious task.
     */
    boolean verifyWebhook(String body, Map<String, String> headers);
}

