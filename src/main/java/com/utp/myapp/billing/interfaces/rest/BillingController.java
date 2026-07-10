package com.utp.myapp.billing.interfaces.rest;

import com.utp.myapp.billing.application.dto.SubscriptionStatusDto;
import com.utp.myapp.billing.application.handler.GetSubscriptionStatusQueryHandler;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Subscription status for the currently authenticated tenant. Used by the
 * dashboard widget "Tu plan actual" and by the upgrade flow.
 */
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final GetSubscriptionStatusQueryHandler getSubscriptionStatus;

    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<SubscriptionStatusDto>> getSubscription() {
        return ResponseEntity.ok(ApiResponse.ok(getSubscriptionStatus.handle()));
    }
}
