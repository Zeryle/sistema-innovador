package com.utp.myapp.billing.application.dto;

import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Snapshot of the current tenant's subscription, plus the relevant catalog
 * context. Returned by {@code GET /api/billing/subscription}.
 *
 * The frontend uses this to render the "Tu plan actual" widget and to know
 * which features to enable in the dashboard.
 */
public record SubscriptionStatusDto(
    String tenantId,
    SubscriptionPlan currentPlan,
    SubscriptionPlan lastPaidPlan,        // the highest paid plan they ever had
    String currentPlanName,
    BigDecimal currentMonthlyPrice,
    String currency,
    LocalDateTime trialEndsAt,            // null = no active trial
    LocalDateTime nextBillingAt,          // null = free plan
    long currentCustomers,
    long currentWorkOrdersThisMonth,
    int currentAdminUsers,
    long maxCustomers,
    int maxAdminUsers,
    long maxWorkOrdersPerMonth,
    boolean whatsappEnabled,
    boolean analyticsEnabled,
    boolean prioritySupport,
    boolean overCustomerLimit,
    boolean overWorkOrderLimit,
    boolean overAdminLimit,
    List<PlanDto> availableUpgrades
) {
    public record PlanDto(
        String code,
        String name,
        String tagline,
        BigDecimal monthlyPrice,
        String currency,
        long maxCustomers,
        int maxAdminUsers,
        long maxWorkOrdersPerMonth,
        String maxCustomersDisplay,
        String maxWorkOrdersDisplay,
        boolean whatsappEnabled,
        boolean analyticsEnabled,
        boolean prioritySupport,
        String[] features
    ) {}
}
