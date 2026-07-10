package com.utp.myapp.whatsapp.interfaces.rest;

import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.whatsapp.application.handler.SendWhatsAppTemplateHandler;
import com.utp.myapp.whatsapp.application.service.AutoReplyService;
import com.utp.myapp.whatsapp.application.service.WhatsAppTemplateService;
import com.utp.myapp.whatsapp.domain.model.aggregates.AutoReplyRule;
import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.repository.IAutoReplyRuleRepository;
import com.utp.myapp.whatsapp.domain.model.repository.IWhatsAppMessageRepository;
import com.utp.myapp.whatsapp.domain.model.templates.WhatsAppTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    private final IWhatsAppMessageRepository repository;
    private final IAutoReplyRuleRepository ruleRepository;
    private final SendWhatsAppTemplateHandler sendHandler;
    private final WhatsAppTemplateService templateService;
    private final AutoReplyService autoReplyService;

    @GetMapping("/inbox")
    public ResponseEntity<ApiResponse<List<WhatsAppMessage>>> getInbox() {
        return ResponseEntity.ok(ApiResponse.ok(repository.findInbox(TenantContext.getTenantId())));
    }

    @GetMapping("/outbox")
    public ResponseEntity<ApiResponse<List<WhatsAppMessage>>> getOutbox() {
        return ResponseEntity.ok(ApiResponse.ok(repository.findOutbox(TenantContext.getTenantId())));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {
        String tenantId = TenantContext.getTenantId();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("unreadInbound", repository.countUnreadInbound(tenantId));
        m.put("inboxCount", repository.findInbox(tenantId).size());
        m.put("outboxCount", repository.findOutbox(tenantId).size());
        return ResponseEntity.ok(ApiResponse.ok(m));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<WhatsAppMessage>> send(@RequestBody SendRequest req) {
        String tenantId = TenantContext.getTenantId();
        String[] params = req.params() != null ? req.params() : new String[0];
        WhatsAppMessage m = sendHandler.send(tenantId, null, req.phone(),
                req.templateName() != null ? req.templateName() : WhatsAppTemplate.HELLO_WORLD.metaName(),
                params);
        return ResponseEntity.ok(ApiResponse.ok(m));
    }

    // ============= Template catalog =============

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listTemplates() {
        return ResponseEntity.ok(ApiResponse.ok(templateService.getCatalog()));
    }

    // ============= Auto-reply rules =============

    @GetMapping("/auto-reply/rules")
    public ResponseEntity<ApiResponse<List<AutoReplyRule>>> listRules() {
        return ResponseEntity.ok(ApiResponse.ok(ruleRepository.findActiveByTenantId(TenantContext.getTenantId())));
    }

    @PostMapping("/auto-reply/rules")
    public ResponseEntity<ApiResponse<AutoReplyRule>> createRule(@RequestBody RuleRequest req) {
        return ResponseEntity.ok(ApiResponse.created(
                autoReplyService.create(TenantContext.getTenantId(), req.keyword(), req.replyText())));
    }

    @PatchMapping("/auto-reply/rules/{id}")
    public ResponseEntity<ApiResponse<AutoReplyRule>> toggleRule(@PathVariable Long id,
                                                                @RequestBody Map<String, Object> body) {
        boolean active = body.get("active") != null && (Boolean) body.get("active");
        return autoReplyService.toggle(id, active)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error("Rule not found")));
    }

    @DeleteMapping("/auto-reply/rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        autoReplyService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Rule deleted"));
    }

    public record SendRequest(String phone, String templateName, String[] params) {}
    public record RuleRequest(String keyword, String replyText) {}
}
