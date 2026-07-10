package com.utp.myapp.billing.infraestructure.persistence.jpa.entities;

import com.utp.myapp.billing.domain.model.valueobjects.CheckoutStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkout_session",
        indexes = {
                @Index(name = "idx_checkout_tenant", columnList = "tenantId"),
                @Index(name = "idx_checkout_intent", columnList = "providerIntentId"),
                @Index(name = "idx_checkout_status", columnList = "status")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CheckoutSessionEntity {

    @Id
    @Column(length = 80)
    private String id;

    @Column(nullable = false, length = 80)
    private String tenantId;

    @Column(nullable = false, length = 20)
    private String targetPlan;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal expectedAmount;

    @Column(nullable = false, length = 8)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CheckoutStatus status;

    @Column(length = 80)
    private String providerName;        // "mock" today, "stripe" later

    @Column(length = 80)
    private String providerIntentId;

    @Column(nullable = false, length = 64)
    private String idempotencyKey;

    private LocalDateTime succeededAt;
    private LocalDateTime cancelledAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
