package com.utp.myapp.whatsapp.interfaces.webhook;

import com.utp.myapp.whatsapp.application.handler.ProcessInboundWhatsAppHandler;
import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Receives events from the Meta WhatsApp Cloud API.
 *
 *  - GET  /api/webhook/whatsapp : Meta's webhook challenge handshake.
 *                                   Both mock and live modes handle this.
 *  - POST /api/webhook/whatsapp : Inbound messages and status updates.
 *                                   In live mode this is called by Meta's servers.
 *                                   In mock mode you can call it manually with a
 *                                   curl payload that looks like a Meta message.
 */
@RestController
@RequestMapping("/api/webhook/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {

    private final IWhatsAppGateway whatsAppGateway;
    private final ProcessInboundWhatsAppHandler inboundHandler;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        // In mock mode the gateway always returns the challenge to ease local testing.
        // In live mode it checks the token against the configured verifyToken.
        String result = whatsAppGateway.verifyWebhook(mode, token, challenge);
        if (result == null || result.isEmpty()) {
            log.warn("WhatsApp webhook verification rejected (mode={}, token provided={})", mode, token);
            return ResponseEntity.status(403).body("Forbidden");
        }
        log.info("WhatsApp webhook verified, mode={}", mode);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<String> receiveMessage(@RequestBody String payload) {
        // Meta sends statuses (delivered, read) AND messages. We only process
        // inbound messages here; status updates could be wired in a follow-up.
        if (payload == null || payload.isEmpty()) {
            return ResponseEntity.ok("OK");
        }
        int processed = inboundHandler.process(payload);
        log.info("WhatsApp webhook processed {} inbound message(s)", processed);
        // Meta expects 200 OK promptly; we ack fast and process sync.
        return ResponseEntity.ok("OK");
    }
}
