package com.utp.myapp.tenant.application.handler;

import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.application.assembler.TenantAssembler;
import com.utp.myapp.tenant.application.command.CreateTenantCommand;
import com.utp.myapp.tenant.application.dto.TenantDto;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTenantCommandHandler {

    private final ITenantRepository tenantRepository;
    private final TenantAssembler tenantAssembler;

    @Transactional
    public TenantDto handle(CreateTenantCommand command) {
        TenantId tenantId = TenantId.generate();
        Tenant tenant = Tenant.create(
                tenantId,
                command.getBusinessName(),
                command.getPhone(),
                command.getPlan()
        );
        Tenant saved = tenantRepository.save(tenant);
        return tenantAssembler.toDto(saved);
    }
}
