package com.utp.myapp.billing.application.handler;

import com.utp.myapp.billing.application.command.StartUpgradeCheckoutCommand;
import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.gateway.GatewayCheckout;
import com.utp.myapp.billing.domain.model.gateway.PaymentGateway;
import com.utp.myapp.billing.domain.model.repository.ICheckoutRepository;
import com.utp.myapp.shared.domain.model.exceptions.DomainException;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.domain.model.aggregates.Tenant;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Creates a CheckoutSession for the requested plan upgrade and asks the
 * payment gateway for a hosted redirect URL.
 *
 * Why per-tenant isolation matters here: a malicious user trying to upgrade
 * a tenant other than their own would only find their session id impossible
 * to associate with any tenant at webhook time, so the webhook handler
 * (next step) can safely reject it.
 */
@Service
@RequiredArgsConstructor
public class StartUpgradeCheckoutCommandHandler {

    private final ICheckoutRepository checkoutRepo;
    private final ITenantRepository tenantRepo;
    private final PaymentGateway gateway;

    @Transactional
    public GatewayCheckout handle(StartUpgradeCheckoutCommand cmd, String currentTenantId) {
        Tenant tenant = tenantRepo.findById(TenantId.of(currentTenantId))
                .orElseThrow(() -> new DomainException("Tenant not found", "TENANT_NOT_FOUND"));

        // Already on that plan? Refuse to charge twice.
        if (tenant.getPlan() == cmd.getTargetPlan()) {
            throw new DomainException("Ya estás en ese plan", "ALREADY_ON_PLAN");
        }
        // Downgrade is also a flow, but for now we only let people upgrade.
        if (tenant.getPlan() != null
                && tenant.getPlan().ordinal() > cmd.getTargetPlan().ordinal()) {
            throw new DomainException("Para bajar de plan contáctanos", "DOWNGRADE_NOT_SUPPORTED");
        }

        CheckoutSession session = CheckoutSession.open(
                TenantId.of(currentTenantId),
                cmd.getTargetPlan(),
                cmd.getTargetPlan().monthlyPrice(),
                cmd.getTargetPlan().currency(),
                cmd.getIdempotencyKey() != null ? cmd.getIdempotencyKey() : UUID.randomUUID().toString()
        );
        // Pre-assign id so the URL contains it before persistence
        session.assignId("mock_cs_" + UUID.randomUUID().toString().replace("-", ""));
        checkoutRepo.insert(session);

        return gateway.createCheckout(session, cmd.getSuccessUrl(), cmd.getCancelUrl());
    }
}
