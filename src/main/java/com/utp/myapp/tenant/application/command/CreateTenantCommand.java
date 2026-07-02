package com.utp.myapp.tenant.application.command;

import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CreateTenantCommand {
    private final String businessName;
    private final String phone;
    private final SubscriptionPlan plan;
}
