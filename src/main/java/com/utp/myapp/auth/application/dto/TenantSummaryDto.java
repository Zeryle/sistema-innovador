package com.utp.myapp.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantSummaryDto {
    private String id;
    private String businessName;
    private String plan;
}
