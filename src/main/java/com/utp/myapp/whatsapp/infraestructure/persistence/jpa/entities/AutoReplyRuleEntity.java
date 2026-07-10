package com.utp.myapp.whatsapp.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "whatsapp_auto_reply",
        indexes = {
                @Index(name = "idx_war_tenant", columnList = "tenantId"),
                @Index(name = "idx_war_active", columnList = "active")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AutoReplyRuleEntity extends BaseEntity {
    @Column(name = "keyword", nullable = false, length = 80)
    private String keyword;

    @Column(name = "reply_text", nullable = false, columnDefinition = "TEXT")
    private String replyText;

    @Column(name = "active", nullable = false)
    private boolean active;
}
