package com.utp.myapp.whatsapp.infraestructure.meta;

import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real Meta Cloud API adapter. NOT registered as @Primary — the Mock gateway
 * is. To go live, set {@code whatsapp.meta.mode=live} in application.properties
 * AND remove the @Primary from MockWhatsAppGateway.
 *
 * In its current form this class is a STUB: it does NOT actually call
 * graph.facebook.com. Instead it logs the JSON payload it would have sent,
 * and returns a deterministic fake message id so the rest of the pipeline
 * (DB persistence, inbox UI, etc.) can be exercised end-to-end without
 * needing real Meta credentials.
 *
 * To turn it into a live implementation:
 *   1) Inject a RestTemplate or WebClient bean (e.g. via WebClient.Builder
 *      configured with a default Authorization header from the access token).
 *   2) In sendTemplate / sendText, POST to
 *      https://graph.facebook.com/{apiVersion}/{phoneNumberId}/messages
 *      with the appropriate body.
 *   3) Parse the response and return the real wamid.
 *
 * See https://developers.facebook.com/docs/whatsapp/cloud-api/reference/messages
 * for the full request/response shape.
 */
@Component
@ConditionalOnProperty(prefix = "whatsapp.meta", name = "mode", havingValue = "live")
public class MetaCloudApiAdapter implements IWhatsAppGateway {

    private static final Logger log = LoggerFactory.getLogger(MetaCloudApiAdapter.class);

    private final WhatsAppProperties props;

    public MetaCloudApiAdapter(WhatsAppProperties props) {
        this.props = props;
        log.info("MetaCloudApiAdapter booted with phoneNumberId={}, waba={}, apiVersion={}",
                props.getPhoneNumberId(), props.getBusinessAccountId(), props.getApiVersion());
    }

    @Override
    public void sendTemplate(String toPhone, String templateName, String... parameters) {
        // STUB: in production this would POST to:
        //   POST https://graph.facebook.com/{apiVersion}/{phoneNumberId}/messages
        //   Authorization: Bearer {accessToken}
        //   Content-Type: application/json
        //   {
        //     "messaging_product": "whatsapp",
        //     "to": "{toPhone}",
        //     "type": "template",
        //     "template": {
        //       "name": "{templateName}",
        //       "language": { "code": "es_PE" },
        //       "components": [
        //         { "type": "body", "parameters": [ {"type":"text","text":"p1"}, ... ] }
        //       ]
        //     }
        //   }
        log.info("[STUB Meta] Would POST template '{}' to {} with params: {}",
                templateName, toPhone, String.join(", ", parameters));
    }

    @Override
    public void sendText(String toPhone, String message) {
        // STUB: in production this would POST to the same endpoint with
        // type=text and body={text:{body:"{message}"}}.
        log.info("[STUB Meta] Would POST text to {}: {}", toPhone, message);
    }

    @Override
    public String verifyWebhook(String mode, String token, String challenge) {
        // In live mode we MUST check the token against the configured verify token.
        // If they match, return the challenge so Meta accepts the webhook.
        if ("subscribe".equals(mode) && props.getVerifyToken().equals(token)) {
            log.info("[Meta] Webhook verified, returning challenge {}", challenge);
            return challenge;
        }
        log.warn("[Meta] Webhook verification failed: mode={}, token provided={}", mode, token);
        return "";
    }
}
