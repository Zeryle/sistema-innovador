package com.utp.myapp.billing.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Command emitted by the webhook handler.
 * Receives the provider's payload (or the mock's synthetic payload) and
 * finds / advances the matching checkout session.
 */
@Getter
@RequiredArgsConstructor
@Builder
public class ProcessStripeWebhookCommand {
    /** Stripe id or our mock id (cs_...). */
    private final String checkoutSessionId;
    /** "succeeded" | "cancelled" — Stripe translates from "payment_intent.succeeded" / "checkout.session.expired". */
    private final String eventType;
    /** Optional provider payment-intent id. */
    private final String providerIntentId;
}
