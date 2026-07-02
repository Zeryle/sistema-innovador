package com.utp.myapp.tenant.application.assembler;

import com.utp.myapp.tenant.application.dto.TenantDto;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantAssembler {

    public TenantDto toDto(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        return TenantDto.builder()
                .id(tenant.getId().value())
                .businessName(tenant.getBusinessName())
                .ruc(tenant.getRuc())
                .phone(tenant.getPhone())
                .logoUrl(tenant.getLogoUrl())
                .plan(tenant.getPlan())
                .build();
    }
}
