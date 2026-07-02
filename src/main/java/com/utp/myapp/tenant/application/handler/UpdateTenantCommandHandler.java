package com.utp.myapp.tenant.application.handler;

import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.application.assembler.TenantAssembler;
import com.utp.myapp.tenant.application.command.UpdateTenantCommand;
import com.utp.myapp.tenant.application.dto.TenantDto;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTenantCommandHandler {

    private final ITenantRepository tenantRepository;
    private final TenantAssembler tenantAssembler;

    @Transactional
    public TenantDto handle(UpdateTenantCommand command) {
        TenantId tenantId = TenantId.of(command.getTenantId());
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", command.getTenantId()));

        tenant.updateBusinessInfo(
                command.getBusinessName() != null ? command.getBusinessName() : tenant.getBusinessName(),
                command.getPhone() != null ? command.getPhone() : tenant.getPhone(),
                command.getRuc() != null ? command.getRuc() : tenant.getRuc()
        );

        if (command.getLogoUrl() != null) {
            tenant.updateLogo(command.getLogoUrl());
        }

        if (command.getPlan() != null) {
            tenant.updatePlan(command.getPlan());
        }

        Tenant saved = tenantRepository.save(tenant);
        return tenantAssembler.toDto(saved);
    }
}
