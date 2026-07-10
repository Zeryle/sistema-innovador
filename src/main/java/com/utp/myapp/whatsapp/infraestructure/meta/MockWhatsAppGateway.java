package com.utp.myapp.whatsapp.infraestructure.meta;

import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of IWhatsAppGateway for development and demos.
 *
 * Activated when whatsapp.meta.mode is unset or "mock" (the default).
 * Logs every "sent" message to the application log instead of calling
 * Meta. The webhook challenge is always accepted to ease local testing.
 *
 * To go live: set whatsapp.meta.mode=live and provide accessToken /
 * phoneNumberId / verifyToken in application.properties (or env vars).
 */
@Component
@ConditionalOnProperty(prefix = "whatsapp.meta", name = "mode", havingValue = "mock", matchIfMissing = true)
public class MockWhatsAppGateway implements IWhatsAppGateway {

    private static final Logger log = LoggerFactory.getLogger(MockWhatsAppGateway.class);

    @Override
    public void sendTemplate(String toPhone, String templateName, String... parameters) {
        log.info("[MOCK WhatsApp] Template '{}' -> {} | params: {}",
                templateName, toPhone, String.join(", ", parameters));
    }

    @Override
    public void sendText(String toPhone, String message) {
        log.info("[MOCK WhatsApp] Text -> {} | body: {}", toPhone, message);
    }

    @Override
    public String verifyWebhook(String mode, String token, String challenge) {
        log.info("[MOCK WhatsApp] Webhook challenge accepted (mode={}, challenge={})", mode, challenge);
        return challenge;
    }
}
