package com.utp.myapp.whatsapp.domain.model.aggregates;

import com.utp.myapp.shared.domain.model.aggregates.AuditableAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * A single auto-reply rule: when a customer message matches the keyword,
 * the system sends back a pre-defined text message. Stored per tenant so
 * each workshop can configure their own.
 *
 * Matching is simple: keyword.toLowerCase() is checked against the
 * customer message (also lowercased). Wildcards are not supported in this
 * iteration; a future version could add regex support.
 */
@Getter
@Setter
public class AutoReplyRule extends AuditableAggregateRoot {

    private Long id;
    private String tenantId;
    /** Lowercased keyword. Matched case-insensitively as substring. */
    private String keyword;
    /** Reply text sent back to the customer. */
    private String replyText;
    /** Whether the rule is currently active. */
    private boolean active;

    public AutoReplyRule() {}

    public static AutoReplyRule create(String tenantId, String keyword, String replyText) {
        AutoReplyRule r = new AutoReplyRule();
        r.tenantId = tenantId;
        r.keyword = keyword == null ? "" : keyword.toLowerCase();
        r.replyText = replyText == null ? "" : replyText;
        r.active = true;
        r.createdAt = java.time.LocalDateTime.now();
        r.updatedAt = r.createdAt;
        return r;
    }

    public boolean matches(String inboundMessage) {
        if (!active) return false;
        if (keyword == null || keyword.isEmpty()) return false;
        if (inboundMessage == null) return false;
        return inboundMessage.toLowerCase().contains(keyword);
    }
}
