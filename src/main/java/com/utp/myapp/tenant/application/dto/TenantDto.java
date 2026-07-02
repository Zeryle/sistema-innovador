package com.utp.myapp.tenant.application.dto;

import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantDto {
    private String id;
    private String businessName;
    private String ruc;
    private String phone;
    private String logoUrl;
    private SubscriptionPlan plan;
}
