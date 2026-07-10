package com.utp.myapp.whatsapp.infrastructure.persistence.jpa.mappers;

import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.infrastructure.persistence.jpa.entities.WhatsAppMessageEntity;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppMessageMapper {

    public WhatsAppMessage toDomain(WhatsAppMessageEntity e) {
        if (e == null) return null;
        return new WhatsAppMessage.Builder()
                .id(e.getId())
                .tenantId(e.getTenantId())
                .customerId(e.getCustomerId())
                .phone(e.getPhone())
                .templateName(e.getTemplateName())
                .direction(e.getDirection())
                .status(e.getStatus())
                .build();
    }

    public WhatsAppMessageEntity toEntity(WhatsAppMessage m) {
        if (m == null) return null;
        WhatsAppMessageEntity e = WhatsAppMessageEntity.builder()
                .phone(m.getPhone())
                .customerId(m.getCustomerId())
                .templateName(m.getTemplateName())
                .direction(m.getDirection())
                .status(m.getStatus() != null ? m.getStatus() : com.utp.myapp.whatsapp.domain.model.valueobjects.MessageStatus.PENDING)
                .sentAt(m.getSentAt())
                .deliveredAt(m.getDeliveredAt())
                .readAt(m.getReadAt())
                .externalMessageId(m.getExternalMessageId())
                .build();
        e.setTenantId(m.getTenantId());
        e.setCreatedAt(m.getCreatedAt());
        e.setUpdatedAt(m.getUpdatedAt());
        return e;
    }
}
