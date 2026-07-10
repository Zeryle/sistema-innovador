package com.utp.myapp.tenant.application.handler;

import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.application.command.UpgradeTenantPlanCommand;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Applies a {@link UpgradeTenantPlanCommand} directly to the {@link Tenant}.
 *
 * @Service here so the Spring container picks it up automatically, even though
 * the only known caller today is the billing webhook handler.
 */
@Service
public class UpgradeTenantPlanCommandHandler {

    private final ITenantRepository tenantRepo;

    public UpgradeTenantPlanCommandHandler(ITenantRepository tenantRepo) {
        this.tenantRepo = tenantRepo;
    }

    @Transactional
    public Tenant handle(UpgradeTenantPlanCommand cmd) {
        Tenant tenant = tenantRepo.findById(cmd.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + cmd.getTenantId().value()));
        tenant.updatePlan(cmd.getNewPlan());
        return tenantRepo.save(tenant);
    }
}
