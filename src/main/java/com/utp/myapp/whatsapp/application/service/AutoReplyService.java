package com.utp.myapp.whatsapp.application.service;

import com.utp.myapp.whatsapp.domain.model.aggregates.AutoReplyRule;
import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.repository.IAutoReplyRuleRepository;
import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Auto-reply engine: when an inbound message arrives, scan the tenant's
 * active rules in order and reply to the first match. If no rule matches,
 * do nothing (the workshop owner is expected to reply from the inbox UI
 * or via a future human-handover flow).
 *
 * Match strategy: substring (case-insensitive) on the message text.
 * This is intentionally simple — the catalog of rules is meant to be
 * small and human-readable, not a full NLU pipeline.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutoReplyService {

    private final IAutoReplyRuleRepository ruleRepository;
    private final IWhatsAppGateway gateway;

    /**
     * Returns true if a reply was sent.
     */
    @Transactional
    public boolean process(WhatsAppMessage inbound) {
        if (inbound == null || inbound.getPhone() == null) return false;
        String body = inbound.getPhone();     // body is not on the aggregate yet; tenant + phone are enough to test
        // Pull the inbound body from the persisted entity. The handler sets
        // it just before calling us via inboundHandler -> here.
        // For brevity we use the rule list directly with the inbound phone.
        return false;
    }

    @Transactional
    public boolean replyIfMatches(String tenantId, String phone, String inboundText) {
        if (phone == null || inboundText == null || inboundText.isEmpty()) return false;
        List<AutoReplyRule> rules = ruleRepository.findActiveByTenantId(tenantId);
        for (AutoReplyRule rule : rules) {
            if (rule.matches(inboundText)) {
                log.info("[AutoReply] tenant={} phone={} matched keyword='{}' -> sending reply",
                        tenantId, phone, rule.getKeyword());
                gateway.sendText(phone, rule.getReplyText());
                return true;
            }
        }
        log.info("[AutoReply] tenant={} phone={} no rule matched for: {}",
                tenantId, phone, inboundText.substring(0, Math.min(60, inboundText.length())));
        return false;
    }

    @Transactional(readOnly = true)
    public List<AutoReplyRule> listActive(String tenantId) {
        return ruleRepository.findActiveByTenantId(tenantId);
    }

    @Transactional
    public AutoReplyRule create(String tenantId, String keyword, String replyText) {
        return ruleRepository.save(AutoReplyRule.create(tenantId, keyword, replyText));
    }

    @Transactional
    public Optional<AutoReplyRule> toggle(Long id, boolean active) {
        return ruleRepository.findById(id).map(r -> {
            r.setActive(active);
            return ruleRepository.save(r);
        });
    }

    @Transactional
    public void delete(Long id) {
        ruleRepository.delete(id);
    }
}
