package com.utp.myapp.whatsapp.interfaces.rest;

import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import com.utp.myapp.whatsapp.domain.model.ports.IWhatsAppGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    private final IWhatsAppGateway whatsAppGateway;

    @PostMapping("/send-template")
    public ResponseEntity<ApiResponse<Void>> sendTemplate(
            @RequestParam String toPhone,
            @RequestParam String templateName,
            @RequestParam(required = false) String param1,
            @RequestParam(required = false) String param2,
            @RequestParam(required = false) String param3) {
        whatsAppGateway.sendTemplate(toPhone, templateName, param1, param2, param3);
        return ResponseEntity.ok(ApiResponse.ok(null, "Message sent (mock)"));
    }

    @PostMapping("/send-text")
    public ResponseEntity<ApiResponse<Void>> sendText(
            @RequestParam String toPhone,
            @RequestParam String message) {
        whatsAppGateway.sendText(toPhone, message);
        return ResponseEntity.ok(ApiResponse.ok(null, "Message sent (mock)"));
    }
}
