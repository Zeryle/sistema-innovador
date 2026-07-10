package com.utp.myapp.tenant.application.command;

import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;

/**
 * Sets the tenant's plan to the given value. Called from the billing webhook
 * on a SUCCEEDED checkout.
 */
@Getter
@RequiredArgsConstructor
@Builder
public class UpgradeTenantPlanCommand {
    private final TenantId tenantId;
    private final SubscriptionPlan newPlan;
}
