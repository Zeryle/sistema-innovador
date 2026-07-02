package com.utp.myapp.tenant.application.command;

import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UpdateTenantCommand {
    private final String tenantId;
    private final String businessName;
    private final String phone;
    private final String ruc;
    private final String logoUrl;
    private final SubscriptionPlan plan;
}
