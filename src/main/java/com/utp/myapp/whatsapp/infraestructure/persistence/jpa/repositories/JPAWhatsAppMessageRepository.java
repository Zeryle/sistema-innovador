package com.utp.myapp.whatsapp.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageDirection;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageStatus;
import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.entities.WhatsAppMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPAWhatsAppMessageRepository extends JpaRepository<WhatsAppMessageEntity, Long> {
    Page<WhatsAppMessageEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId, Pageable pageable);
    List<WhatsAppMessageEntity> findByTenantIdAndDirectionOrderByCreatedAtDesc(String tenantId, MessageDirection direction);
    List<WhatsAppMessageEntity> findByTenantIdAndStatus(String tenantId, MessageStatus status);
    long countByTenantIdAndDirectionAndStatus(String tenantId, MessageDirection direction, MessageStatus status);
}
