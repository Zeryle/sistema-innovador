package com.utp.myapp.whatsapp.domain.model.ports;

/**
 * Output port for sending WhatsApp messages.
 * Implementations will use Meta Cloud API (real) or a mock for development.
 */
public interface IWhatsAppGateway {

    /** Send a template message via WhatsApp */
    void sendTemplate(String toPhone, String templateName, String... parameters);

    /** Send a free text message via WhatsApp */
    void sendText(String toPhone, String message);

    /** Verify webhook challenge from Meta */
    String verifyWebhook(String mode, String token, String challenge);
}
