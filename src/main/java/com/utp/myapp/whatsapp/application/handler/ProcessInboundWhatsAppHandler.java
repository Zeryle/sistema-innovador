package com.utp.myapp.whatsapp.application.handler;

import com.utp.myapp.whatsapp.application.service.AutoReplyService;
import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.repository.IWhatsAppMessageRepository;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageDirection;
import com.utp.myapp.whatsapp.domain.model.valueobjects.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses an inbound WhatsApp webhook payload (Meta Cloud API shape), persists
 * a {@link WhatsAppMessage} for each entry, and runs the auto-reply engine.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessInboundWhatsAppHandler {

    private static final Pattern FROM_P = Pattern.compile("\"from\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern BODY_P = Pattern.compile("\"body\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern WAMID_P = Pattern.compile("\"id\"\\s*:\\s*\"(wamid\\.[^\"]+)\"");

    private final IWhatsAppMessageRepository repository;
    private final AutoReplyService autoReplyService;

    @Transactional
    public int process(String payload) {
        if (payload == null || payload.isEmpty()) return 0;
        int count = 0;
        String body = "";
        Matcher bm = BODY_P.matcher(payload);
        if (bm.find()) body = bm.group(1);

        Matcher fromM = FROM_P.matcher(payload);
        while (fromM.find()) {
            String phone = fromM.group(1);
            String externalId = "";
            Matcher im = WAMID_P.matcher(payload);
            if (im.find()) externalId = im.group(1);

            WhatsAppMessage m = new WhatsAppMessage.Builder()
                    .tenantId("test-tenant-001")
                    .phone(phone)
                    .templateName(null)
                    .direction(MessageDirection.INBOUND)
                    .status(MessageStatus.DELIVERED)
                    .build();
            repository.save(m);

            log.info("[WhatsApp INBOUND] from {} body={} (wamid={})", phone, body, externalId);
            // Try auto-reply. Failures here must not affect the inbound persistence,
            // so we don't rethrow — the inbound is already saved.
            try {
                autoReplyService.replyIfMatches(m.getTenantId(), phone, body);
            } catch (Exception ex) {
                log.error("Auto-reply failed (inbound already saved): {}", ex.getMessage());
            }
            count++;
        }
        return count;
    }
}
