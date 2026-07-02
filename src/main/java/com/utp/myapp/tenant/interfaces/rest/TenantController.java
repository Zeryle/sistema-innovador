package com.utp.myapp.tenant.interfaces.rest;

import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.tenant.application.command.UpdateTenantCommand;
import com.utp.myapp.tenant.application.dto.TenantDto;
import com.utp.myapp.tenant.application.handler.UpdateTenantCommandHandler;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.application.assembler.TenantAssembler;
import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final ITenantRepository tenantRepository;
    private final TenantAssembler tenantAssembler;
    private final UpdateTenantCommandHandler updateHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<TenantDto>> getCurrentTenant(Principal principal) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No tenant context found"));
        }

        Tenant tenant = tenantRepository.findById(TenantId.of(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Tenant", tenantId));

        return ResponseEntity.ok(ApiResponse.ok(tenantAssembler.toDto(tenant)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<TenantDto>> updateTenant(
            @RequestBody UpdateTenantCommand command) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No tenant context found"));
        }

        UpdateTenantCommand enrichedCommand = UpdateTenantCommand.builder()
                .tenantId(tenantId)
                .businessName(command.getBusinessName())
                .phone(command.getPhone())
                .ruc(command.getRuc())
                .logoUrl(command.getLogoUrl())
                .plan(command.getPlan())
                .build();

        TenantDto result = updateHandler.handle(enrichedCommand);
        return ResponseEntity.ok(ApiResponse.ok(result, "Tenant updated successfully"));
    }
}
