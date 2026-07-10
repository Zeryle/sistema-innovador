package com.utp.myapp.whatsapp.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageDirection;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_message",
        indexes = {
                @Index(name = "idx_wam_tenant", columnList = "tenantId"),
                @Index(name = "idx_wam_phone", columnList = "phone"),
                @Index(name = "idx_wam_status", columnList = "status"),
                @Index(name = "idx_wam_direction", columnList = "direction")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WhatsAppMessageEntity extends BaseEntity {

    @Column(name = "phone", nullable = false, length = 32)
    private String phone;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "template_name", length = 80)
    private String templateName;

    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 16)
    private MessageDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private MessageStatus status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "external_message_id", length = 80)
    private String externalMessageId;
}
