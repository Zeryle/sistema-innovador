package com.utp.myapp.whatsapp.domain.model.repository;

import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageDirection;

import java.util.List;

/**
 * Repository port for WhatsApp messages (both inbound and outbound).
 */
public interface IWhatsAppMessageRepository {
    WhatsAppMessage save(WhatsAppMessage message);
    List<WhatsAppMessage> findInbox(String tenantId);
    List<WhatsAppMessage> findOutbox(String tenantId);
    long countUnreadInbound(String tenantId);
}
