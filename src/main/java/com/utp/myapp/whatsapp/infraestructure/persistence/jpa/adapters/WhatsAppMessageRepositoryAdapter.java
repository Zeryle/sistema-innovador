package com.utp.myapp.whatsapp.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.repository.IWhatsAppMessageRepository;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageDirection;
import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.mappers.WhatsAppMessageMapper;
import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.repositories.JPAWhatsAppMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WhatsAppMessageRepositoryAdapter implements IWhatsAppMessageRepository {

    private final JPAWhatsAppMessageRepository jpa;
    private final WhatsAppMessageMapper mapper;

    @Override
    public WhatsAppMessage save(WhatsAppMessage message) {
        return mapper.toDomain(jpa.save(mapper.toEntity(message)));
    }

    @Override
    public List<WhatsAppMessage> findInbox(String tenantId) {
        return jpa.findByTenantIdAndDirectionOrderByCreatedAtDesc(tenantId, MessageDirection.INBOUND)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WhatsAppMessage> findOutbox(String tenantId) {
        return jpa.findByTenantIdAndDirectionOrderByCreatedAtDesc(tenantId, MessageDirection.OUTBOUND)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countUnreadInbound(String tenantId) {
        return jpa.countByTenantIdAndDirectionAndStatus(tenantId, MessageDirection.INBOUND,
                com.utp.myapp.whatsapp.domain.model.valueobjects.MessageStatus.DELIVERED);
    }
}
