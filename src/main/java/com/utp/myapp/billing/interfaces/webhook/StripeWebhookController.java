package com.utp.myapp.billing.interfaces.webhook;

import com.utp.myapp.billing.application.command.ProcessStripeWebhookCommand;
import com.utp.myapp.billing.application.handler.ProcessStripeWebhookCommandHandler;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Receives Stripe-style webhook events.
 *
 * Stripe sends real POSTs to https://your-app/api/webhook/stripe with a body
 * containing the event type and resource. Our mock SPA posts to the same URL
 * when the user "pays" on /billing/checkout/{id}/pay.
 *
 * The endpoint is mounted under /api/webhook/ — already whitelisted in
 * SecurityConfig as permitAll.
 *
 * Body shape (Stripe "checkout.session.completed" payload abbreviated):
 * <pre>
 * {
 *   "type": "checkout.session.completed",
 *   "data": { "object": { "id": "cs_...", "payment_intent": "pi_..." } }
 * }
 * </pre>
 *
 * Our mock just sends:
 * <pre>
 * { "type": "succeeded", "sessionId": "mock_cs_...", "providerIntentId": "..." }
 * </pre>
 */
@RestController
@RequestMapping("/api/webhook/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final ProcessStripeWebhookCommandHandler handler;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> receive(
            @RequestBody String rawBody,
            @RequestHeader Map<String, String> headers) {

        // We accept two shapes:
        //   a) Stripe-like:    {"type":"...", "data":{"object":{"id":"cs_..."}}}
        //   b) Mock-like:      {"type":"succeeded", "sessionId":"mock_cs_..."}
        // We extract the relevant fields by simple string matching. Robust JSON
        // parsing isn't worth the extra dependency for this single endpoint.
        String sessionId = extract(rawBody, "\"id\"\\s*:\\s*\"([^\"]+)\"", "\"sessionId\"\\s*:\\s*\"([^\"]+)\"");
        String intentId = extract(rawBody, "\"payment_intent\"\\s*:\\s*\"([^\"]+)\"", "\"providerIntentId\"\\s*:\\s*\"([^\"]+)\"");
        String type = extract(rawBody, "\"type\"\\s*:\\s*\"([^\"]+)\"", null);

        if (sessionId == null) {
            log.warn("Webhook body missing session id: {}", rawBody);
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing session id"));
        }

        ProcessStripeWebhookCommand cmd = ProcessStripeWebhookCommand.builder()
                .checkoutSessionId(sessionId)
                .eventType(type != null ? type : "succeeded")
                .providerIntentId(intentId)
                .build();
        handler.handle(cmd, rawBody, headers);

        // Stripe-style responses are 200 OK to acknowledge. The real provider
        // also sends 2xx for retry-eligible failures — we follow that.
        return ResponseEntity.ok(ApiResponse.ok(null, "Webhook processed"));
    }

    /**
     * Try the first pattern; if it doesn't match, try the second. Returns
     * null if neither matched. Patterns are pre-compiled for reuse.
     */
    private static final java.util.regex.Pattern[] CACHED = new java.util.regex.Pattern[]{
            java.util.regex.Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\""),
            java.util.regex.Pattern.compile("\"sessionId\"\\s*:\\s*\"([^\"]+)\""),
            java.util.regex.Pattern.compile("\"payment_intent\"\\s*:\\s*\"([^\"]+)\""),
            java.util.regex.Pattern.compile("\"providerIntentId\"\\s*:\\s*\"([^\"]+)\""),
            java.util.regex.Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"")
    };

    private String extract(String body, String primary, String fallback) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(primary).matcher(body);
        if (m.find()) return m.group(1);
        if (fallback != null) {
            m = java.util.regex.Pattern.compile(fallback).matcher(body);
            if (m.find()) return m.group(1);
        }
        return null;
    }
}