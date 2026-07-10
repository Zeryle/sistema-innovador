package com.utp.myapp.billing.application.handler;

import com.utp.myapp.billing.application.query.GetCheckoutQuery;
import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.gateway.PaymentGateway;
import com.utp.myapp.billing.domain.model.repository.ICheckoutRepository;
import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Returns the data needed by the SPA to render the mock payment page.
 * We don't expose the full aggregate (which has internal ids / state machine
 * fields) — just the public-facing bits plus a synthesized "intentId" so
 * the page can POST it back to the webhook.
 */
@Service
@RequiredArgsConstructor
public class GetCheckoutQueryHandler {

    private final ICheckoutRepository checkoutRepo;

    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> handle(GetCheckoutQuery q) {
        return checkoutRepo.listById(q.getCheckoutSessionId())
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", s.id());
                    m.put("status", s.status());
                    m.put("targetPlan", s.targetPlan().name());
                    m.put("targetPlanName", s.targetPlan().displayName());
                    m.put("expectedAmount", s.expectedAmount());
                    m.put("currency", s.currency());
                    m.put("tenantId", s.tenantId().value());
                    m.put("providerIntentId", s.paymentIntent() != null
                            ? s.paymentIntent().intentId()
                            : s.id());     // mock: pi id == cs id
                    m.put("isFinal", s.isFinal());
                    return m;
                });
    }
}
