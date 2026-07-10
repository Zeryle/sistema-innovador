package com.utp.myapp.whatsapp.application.service;

import com.utp.myapp.whatsapp.application.handler.SendWhatsAppTemplateHandler;
import com.utp.myapp.whatsapp.domain.model.aggregates.WhatsAppMessage;
import com.utp.myapp.whatsapp.domain.model.templates.WhatsAppTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * High-level service that turns a typed template name + structured parameters
 * into a real WhatsApp send. This is the API the rest of the app uses; it
 * shields the reminder/notification code from having to know the Meta
 * template naming convention or the placeholder order.
 *
 * Examples:
 *   whatsappTemplateService.sendRecordatorioMantenimiento(tenantId, customerId, phone,
 *       "Mantenimiento programado", "Corolla 2020", "Jul 17 2:59 PM");
 *   whatsappTemplateService.sendOrdenRecibida(tenantId, customerId, phone, "ABC-123", "Carlos Garcia");
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppTemplateService {

    private final SendWhatsAppTemplateHandler sendHandler;

    public WhatsAppMessage sendHelloWorld(String tenantId, Integer customerId, String phone) {
        return send(tenantId, customerId, phone, WhatsAppTemplate.HELLO_WORLD);
    }

    public WhatsAppMessage sendRecordatorioMantenimiento(String tenantId, Integer customerId, String phone,
                                                       String titulo, String vehiculo, String fecha) {
        return send(tenantId, customerId, phone, WhatsAppTemplate.RECORDATORIO_MANTENIMIENTO,
                titulo, vehiculo, fecha);
    }

    public WhatsAppMessage sendOrdenRecibida(String tenantId, Integer customerId, String phone,
                                            String placa, String cliente) {
        return send(tenantId, customerId, phone, WhatsAppTemplate.ORDEN_RECIBIDA,
                placa, cliente);
    }

    public WhatsAppMessage sendOrdenCompletada(String tenantId, Integer customerId, String phone,
                                              String placa, String cliente, String costo) {
        return send(tenantId, customerId, phone, WhatsAppTemplate.ORDEN_COMPLETADA,
                placa, cliente, costo);
    }

    public WhatsAppMessage sendRecordatorioCancelado(String tenantId, Integer customerId, String phone,
                                                    String titulo, String motivo) {
        return send(tenantId, customerId, phone, WhatsAppTemplate.RECORDATORIO_CANCELADO,
                titulo, motivo);
    }

    public WhatsAppMessage sendPromocionMantenimiento(String tenantId, Integer customerId, String phone,
                                                     String nombre, String vehiculo, String descuento) {
        return send(tenantId, customerId, phone, WhatsAppTemplate.PROMOCION_MANTENIMIENTO,
                nombre, vehiculo, descuento);
    }

    /**
     * Generic send. Throws if the number of parameters doesn't match the
     * template's requiredArgs.
     */
    public WhatsAppMessage send(String tenantId, Integer customerId, String phone,
                                WhatsAppTemplate template, String... args) {
        if (args.length != template.requiredArgs()) {
            throw new IllegalArgumentException(
                    "Template " + template.metaName() + " requires " + template.requiredArgs()
                            + " parameters, got " + args.length);
        }
        return sendHandler.send(tenantId, customerId, phone, template.metaName(), args);
    }

    /**
     * Returns the catalog of available templates as a structured list
     * (for the WhatsApp UI page).
     */
    public Map<String, Object> getCatalog() {
        Map<String, Object> out = new LinkedHashMap<>();
        for (WhatsAppTemplate t : WhatsAppTemplate.values()) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("metaName", t.metaName());
            info.put("description", t.description());
            info.put("requiredArgs", t.requiredArgs());
            info.put("placeholders", t.placeholders());
            out.put(t.name(), info);
        }
        return out;
    }
}
