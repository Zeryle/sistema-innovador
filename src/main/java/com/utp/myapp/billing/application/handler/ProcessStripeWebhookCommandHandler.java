
package com.utp.myapp.billing.application.handler;

import com.utp.myapp.billing.application.command.ProcessStripeWebhookCommand;
import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.events.CheckoutSucceededEvent;
import com.utp.myapp.billing.domain.model.gateway.PaymentGateway;
import com.utp.myapp.billing.domain.model.repository.ICheckoutRepository;
import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import com.utp.myapp.billing.domain.model.valueobjects.PaymentProvider;
import com.utp.myapp.shared.domain.model.exceptions.DomainException;
import com.utp.myapp.shared.domain.model.valueobjects.TenantId;
import com.utp.myapp.tenant.application.command.UpgradeTenantPlanCommand;
import com.utp.myapp.tenant.application.handler.UpgradeTenantPlanCommandHandler;
import com.utp.myapp.tenant.domain.model.repository.ITenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Handles Stripe (or Mock) webhook callbacks for our checkout sessions.
 *
 * The mock's SPA page issues an HTTP POST against /api/webhook/stripe with
 * the same JSON shape as Stripe's "checkout.session.completed" /
 * "checkout.session.expired" events. In production, those events would come
 * from Stripe's HTTPS endpoint with HMAC signatures.
 *
 * Lifecycle (per webhook call):
 *   1) gateway.verifyWebhook()  -> if false, reject as 400
 *   2) lookup the session by id
 *   3) transition its status (markProcessing then markSucceeded/Cancelled)
 *   4) on SUCCEEDED: call the tenant UpgradePlan command
 *   5) publish CheckoutSucceededEvent for any future listeners (audit, email, etc.)
 */
@Service
@RequiredArgsConstructor
public class ProcessStripeWebhookCommandHandler {

    private final ICheckoutRepository checkoutRepo;
    private final ITenantRepository tenantRepo;
    private final PaymentGateway gateway;
    private final ApplicationEventPublisher events;
    private final UpgradeTenantPlanCommandHandler upgradeHandler;

    @Transactional
    public void handle(ProcessStripeWebhookCommand cmd, String body, Map<String, String> headers) {
        if (!gateway.verifyWebhook(body, headers)) {
            throw new DomainException("Invalid webhook signature", "WEBHOOK_INVALID");
        }

        CheckoutSession session = checkoutRepo.listById(cmd.getCheckoutSessionId())
                .orElseThrow(() -> new DomainException(
                        "Unknown checkout session", "CHECKOUT_NOT_FOUND"));

        // Idempotency: already succeeded? nothing to do.
        if (session.status() == CheckoutStatus.SUCCEEDED) {
            return;
        }

        switch (cmd.getEventType() == null ? "" : cmd.getEventType().toLowerCase()) {
            case "succeeded":
            case "checkout.session.completed":
            case "checkout.session.async_payment_succeeded": {
                if (cmd.getProviderIntentId() != null) {
                    session.markProcessing(
                            new PaymentProvider("mock", cmd.getProviderIntentId()));
                }
                session.markSucceeded();
                checkoutRepo.update(session);

                upgradeHandler.handle(new UpgradeTenantPlanCommand(
                        TenantId.of(session.tenantId().value()), session.targetPlan()));

                events.publishEvent(new CheckoutSucceededEvent(
                        session.id(),
                        session.tenantId().value(),
                        session.targetPlan().name(),
                        "mock",
                        cmd.getProviderIntentId() != null ? cmd.getProviderIntentId() : session.id()));
                break;
            }
            case "cancelled":
            case "expired":
            case "checkout.session.expired":
                session.markCancelled();
                checkoutRepo.update(session);
                break;
            default:
                // ignore unknown event types (delivery keep-alive, etc.)
                break;
        }
    }
}
