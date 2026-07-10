package com.utp.myapp.whatsapp.application.handler;

import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import com.utp.myapp.whatsapp.domain.model.repository.IWhatsAppMessageRepository;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageDirection;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application-layer service for sending a WhatsApp template message.
 *
 * Two responsibilities:
 *   1) Hand off the actual send to the gateway (mock or live Meta).
 *   2) Persist a record of the message so the inbox / outbox / dashboard
 *      counters reflect what was sent, regardless of which gateway ran.
 */
@Service
@RequiredArgsConstructor
public class SendWhatsAppTemplateHandler {

    private final IWhatsAppGateway gateway;
    private final IWhatsAppMessageRepository repository;

    @Transactional
    public WhatsAppMessage send(String tenantId, Integer customerId, String phone,
                                String templateName, String... params) {
        gateway.sendTemplate(phone, templateName, params);

        String externalId = "mock_" + UUID.randomUUID().toString().substring(0, 12);
        WhatsAppMessage m = new WhatsAppMessage.Builder()
                .tenantId(tenantId)
                .customerId(customerId)
                .phone(phone)
                .templateName(templateName)
                .direction(MessageDirection.OUTBOUND)
                .status(MessageStatus.SENT)
                .build();
        m.markSent(externalId);
        return repository.save(m);
    }
}
