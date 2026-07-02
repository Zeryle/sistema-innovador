package com.utp.myapp.whatsapp.interfaces.webhook;

import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook/whatsapp")
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private final IWhatsAppGateway whatsAppGateway;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        String result = whatsAppGateway.verifyWebhook(mode, token, challenge);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<String> receiveMessage(@RequestBody String payload) {
        // Process inbound WhatsApp message from Meta
        System.out.println("[WHATSAPP INBOUND] " + payload);
        return ResponseEntity.ok("OK");
    }
}
