package com.utp.myapp.billing.domain.model.events;

/**
 * Domain event published when a checkout reaches SUCCEEDED.
 * Listened to by the tenant aggregator handler to commit the plan upgrade
 * (and in the future: send "welcome email", unlock premium features, etc.).
 */
public record CheckoutSucceededEvent(
    String checkoutSessionId,
    String tenantId,
    String newPlanCode,
    String provider,
    String providerIntentId
) {}
