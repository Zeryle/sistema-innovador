package com.utp.myapp.whatsapp.infraestructure.meta;

import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of IWhatsAppGateway for development.
 * Logs messages to console instead of sending via Meta Cloud API.
 * Replace with {@link MetaCloudApiAdapter} when real credentials are available.
 */
@Component
@Primary
public class MockWhatsAppGateway implements IWhatsAppGateway {

    private static final Logger log = LoggerFactory.getLogger(MockWhatsAppGateway.class);

    @Override
    public void sendTemplate(String toPhone, String templateName, String... parameters) {
        log.info("[MOCK WhatsApp] Sending template '{}' to {} with params: {}",
                templateName, toPhone, String.join(", ", parameters));
    }

    @Override
    public void sendText(String toPhone, String message) {
        log.info("[MOCK WhatsApp] Sending text to {}: {}", toPhone, message);
    }

    @Override
    public String verifyWebhook(String mode, String token, String challenge) {
        log.info("[MOCK WhatsApp] Webhook verification: mode={}, token={}, challenge={}", mode, token, challenge);
        return challenge;
    }
}
