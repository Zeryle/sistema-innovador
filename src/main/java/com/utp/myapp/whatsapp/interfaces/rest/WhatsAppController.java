package com.utp.myapp.whatsapp.interfaces.rest;

import com.utp.myapp.shared.infraestructure.config.TenantContext;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.whatsapp.application.handler.SendWhatsAppTemplateHandler;
import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.repository.IWhatsAppMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tenant-facing WhatsApp endpoints:
 *  - GET    /api/whatsapp/inbox   : list inbound messages (customer replies)
 *  - GET    /api/whatsapp/outbox  : list outbound messages (we sent)
 *  - GET    /api/whatsapp/stats   : counters for the dashboard widget
 *  - POST   /api/whatsapp/send    : manual send (testing)
 */
@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    private final IWhatsAppMessageRepository repository;
    private final SendWhatsAppTemplateHandler sendHandler;

    @GetMapping("/inbox")
    public ResponseEntity<ApiResponse<List<WhatsAppMessage>>> getInbox() {
        String tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(repository.findInbox(tenantId)));
    }

    @GetMapping("/outbox")
    public ResponseEntity<ApiResponse<List<WhatsAppMessage>>> getOutbox() {
        String tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(ApiResponse.ok(repository.findOutbox(tenantId)));
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

    /**
     * Manual send (testing the mock pipeline without going through a reminder).
     * Body: { "phone":"+51999888777", "templateName":"hello_world", "params":["..."] }
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<WhatsAppMessage>> send(@RequestBody SendRequest req) {
        String tenantId = TenantContext.getTenantId();
        String[] params = req.params() != null ? req.params() : new String[0];
        WhatsAppMessage m = sendHandler.send(tenantId, null, req.phone(), req.templateName(), params);
        return ResponseEntity.ok(ApiResponse.ok(m));
    }

    public record SendRequest(String phone, String templateName, String[] params) {}
}
