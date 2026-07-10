package com.utp.myapp.whatsapp.domain.model.templates;

import java.util.Arrays;
import java.util.List;

/**
 * Catalog of pre-approved WhatsApp templates the SaaS sends on behalf of a
 * workshop. Templates are part of the WhatsApp Business API contract —
 * each one must be submitted to Meta and approved before it can be used in
 * a non-test scenario. This enum maps our internal template name to the
 * Meta-side identifier and the placeholders our code injects.
 *
 * Meta template language: es_PE for Peru (Peruvian Spanish).
 *
 * Adding a new template:
 *   1) Create it in Meta Business Suite (Templates) and submit for approval.
 *   2) Add a new enum constant here with the same name Meta approved.
 *   3) The render(...) method returns the placeholders in the order Meta
 *      expects them in the components array.
 */
public enum WhatsAppTemplate {
    HELLO_WORLD(
            "hello_world",
            "Saludo inicial al registrarse",
            Arrays.asList()
    ),
    RECORDATORIO_MANTENIMIENTO(
            "recordatorio_mantenimiento",
            "Recordatorio de mantenimiento programado",
            Arrays.asList("{{1}}", "{{2}}", "{{3}}")
    ),
    ORDEN_RECIBIDA(
            "orden_recibida",
            "Notifica al cliente que su vehiculo fue recibido en el taller",
            Arrays.asList("{{1}}", "{{2}}")
    ),
    ORDEN_COMPLETADA(
            "orden_completada",
            "Notifica al cliente que su vehiculo esta listo para entrega",
            Arrays.asList("{{1}}", "{{2}}", "{{3}}")
    ),
    RECORDATORIO_CANCELADO(
            "recordatorio_cancelado",
            "Confirma al cliente que un recordatorio fue cancelado",
            Arrays.asList("{{1}}", "{{2}}")
    ),
    PROMOCION_MANTENIMIENTO(
            "promocion_mantenimiento",
            "Promocion periodica de mantenimiento preventivo",
            Arrays.asList("{{1}}", "{{2}}", "{{3}}")
    );

    private final String metaName;
    private final String description;
    private final List<String> placeholders;

    WhatsAppTemplate(String metaName, String description, List<String> placeholders) {
        this.metaName = metaName;
        this.description = description;
        this.placeholders = placeholders;
    }

    public String metaName() { return metaName; }
    public String description() { return description; }
    public List<String> placeholders() { return placeholders; }
    public int requiredArgs() { return placeholders.size(); }
}
