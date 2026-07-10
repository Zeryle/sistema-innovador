
package com.utp.myapp.billing.domain.model.valueobjects;

/**
 * Lifecycle of a Stripe checkout session (or its mock equivalent).
 * The flow follows Stripe's status field almost 1:1 so that swapping
 * the real Stripe gateway later is a one-line change.
 */
public enum CheckoutStatus {
    OPEN,         // Session created, awaiting payment
    PROCESSING,   // Webhook received, processing payment async
    SUCCEEDED,    // Payment captured, plan upgrade is now committed
    CANCELLED,    // User abandoned the checkout
    EXPIRED       // Session timed out (24h default; not enforced in the mock)
}
