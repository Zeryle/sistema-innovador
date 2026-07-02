package com.utp.myapp.tenant.domain.model.valueobjects;

/**
 * Subscription tiers for the SaaS platform.
 */
public enum SubscriptionPlan {
    FREE,      // Limited features, up to N customers
    BASIC,     // Full features, up to N customers, WhatsApp enabled
    PREMIUM    // Unlimited everything, priority support
}
