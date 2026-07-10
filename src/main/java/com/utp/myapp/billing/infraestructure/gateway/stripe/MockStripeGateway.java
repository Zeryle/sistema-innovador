package com.utp.myapp.billing.infraestructure.gateway.stripe;

import com.utp.myapp.billing.domain.model.aggregates.CheckoutSession;
import com.utp.myapp.billing.domain.model.gateway.GatewayCheckout;
import com.utp.myapp.billing.domain.model.gateway.PaymentGateway;
import com.utp.myapp.billing.domain.model.valueobjects.PaymentProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Mock of Stripe's CheckoutSession API. Lives in the infrastructure layer.
 *
 * Behavior:
 *  - Generates a session id shaped like "mock_cs_&lt;uuid&gt;".
 *  - Returns a frontend URL pointing at /billing/checkout/{id} where the
 *    SPA shows a fake card form. That page POSTs to /api/webhook/stripe
 *    to advance the state machine, mirroring what Stripe's hosted page
 *    does in production.
 *  - verifyWebhook always returns true: in the mock, trust is enough.
 *    The real Stripe gateway would verify an HMAC header here.
 *
 * Marked @Primary so the domain layer binds to this without any qualifier.
 * To go live: remove @Primary from this class, add @Primary to the real
 * StripeGateway class with a populated secret in application-mysql.properties.
 */
@Component
@Primary
public class MockStripeGateway implements PaymentGateway {

    @Override
    public GatewayCheckout createCheckout(CheckoutSession session, String successUrl, String cancelUrl) {
        String id = session.id() != null
                ? session.id()
                : "mock_cs_" + UUID.randomUUID().toString().replace("-", "");
        long expires = Instant.now().plusSeconds(30 * 60).getEpochSecond();   // 30 min
        String url = "/billing/checkout/" + id
                + "?success_url=" + url(successUrl)
                + "&cancel_url=" + url(cancelUrl);
        return new GatewayCheckout(id, PaymentProvider.mock(id), url, expires);
    }

    @Override
    public boolean verifyWebhook(String body, Map<String, String> headers) {
        return true;
    }

    private static String url(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
