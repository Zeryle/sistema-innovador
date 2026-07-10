package com.utp.myapp.billing.interfaces.rest;

import com.utp.myapp.billing.application.command.StartUpgradeCheckoutCommand;
import com.utp.myapp.billing.application.dto.SubscriptionStatusDto;
import com.utp.myapp.billing.application.handler.GetCheckoutQueryHandler;
import com.utp.myapp.billing.application.handler.GetSubscriptionStatusQueryHandler;
import com.utp.myapp.billing.application.handler.StartUpgradeCheckoutCommandHandler;
import com.utp.myapp.billing.application.query.GetCheckoutQuery;
import com.utp.myapp.billing.domain.model.gateway.GatewayCheckout;
import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.tenant.domain.model.valueobjects.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Subscription + checkout endpoints for the currently authenticated tenant.
 *
 * Endpoints:
 *  - GET  /api/billing/subscription           — current subscription status
 *  - POST /api/billing/checkout              — start a Stripe checkout for a target plan
 *  - GET  /api/billing/checkout/{id}         — fetch one checkout session (for the SPA mock payment page)
 */
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final GetSubscriptionStatusQueryHandler getSubscriptionStatus;
    private final StartUpgradeCheckoutCommandHandler startUpgradeCheckout;
    private final GetCheckoutQueryHandler getCheckout;

    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<SubscriptionStatusDto>> getSubscription() {
        return ResponseEntity.ok(ApiResponse.ok(getSubscriptionStatus.handle()));
    }

    /**
     * Request body: {"targetPlan":"BASIC", "successUrl":"/billing/success", "cancelUrl":"/billing/cancel"}
     */
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<GatewayCheckout>> startCheckout(
            @RequestBody StartCheckoutRequest req) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No tenant context"));
        }

        SubscriptionPlan target = SubscriptionPlan.valueOf(req.targetPlan());
        StartUpgradeCheckoutCommand cmd = StartUpgradeCheckoutCommand.builder()
                .targetPlan(target)
                .idempotencyKey(req.idempotencyKey())
                .successUrl(req.successUrl() != null ? req.successUrl() : "/billing/success")
                .cancelUrl(req.cancelUrl() != null ? req.cancelUrl() : "/billing/cancel")
                .build();
        GatewayCheckout result = startUpgradeCheckout.handle(cmd, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }

    @GetMapping("/checkout/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCheckout(@PathVariable String id) {
        return getCheckout.handle(new GetCheckoutQuery(id))
                .map(c -> ResponseEntity.ok(ApiResponse.ok(c)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Checkout session not found")));
    }

    public record StartCheckoutRequest(
            String targetPlan,
            String idempotencyKey,
            String successUrl,
            String cancelUrl) {}
}