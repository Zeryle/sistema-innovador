package com.utp.myapp.whatsapp.domain.model.aggregates;

import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageDirection;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageStatus;

import java.time.LocalDateTime;

public class WhatsAppMessage extends AuditableAggregateRoot {

    private Long id;
    private String tenantId;
    private Integer customerId;
    private String phone;
    private String templateName;
    private String parameters;
    private MessageDirection direction;
    private MessageStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private String externalMessageId;

    private WhatsAppMessage() {}

    public Long getId() { return id; }
    public String getTenantId() { return tenantId; }
    public Integer getCustomerId() { return customerId; }
    public String getPhone() { return phone; }
    public String getTemplateName() { return templateName; }
    public MessageDirection getDirection() { return direction; }
    public MessageStatus getStatus() { return status; }
    public LocalDateTime getSentAt() { return sentAt; }
    public String getExternalMessageId() { return externalMessageId; }
    public String getParameters() { return parameters; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getReadAt() { return readAt; }

    public void markSent(String externalId) {
        this.status = MessageStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.externalMessageId = externalId;
        markUpdated();
    }

    public static class Builder {
        private final WhatsAppMessage m = new WhatsAppMessage();
        public Builder id(Long id) { m.id = id; return this; }
        public Builder tenantId(String id) { m.tenantId = id; return this; }
        public Builder customerId(Integer id) { m.customerId = id; return this; }
        public Builder phone(String p) { m.phone = p; return this; }
        public Builder templateName(String n) { m.templateName = n; return this; }
        public Builder direction(MessageDirection d) { m.direction = d; return this; }
        public Builder status(MessageStatus s) { m.status = s; return this; }
        public WhatsAppMessage build() { return m; }
    }
}
