
package com.utp.myapp.billing.domain.model.valueobjects;

/**
 * Idempotency-friendly identifier for a payment intent.
 * In real Stripe this is "pi_3Oz..." (PaymentIntent id).
 * In our Mock we use "mock_pi_<uuid>" — same shape so callers
 * don't need to special-case the gateway in domain logic.
 */
public record PaymentProvider(String provider, String intentId) {
    public static PaymentProvider mock(String intentId) {
        return new PaymentProvider("mock", intentId);
    }
}
