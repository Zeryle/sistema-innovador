package com.utp.myapp.tenant.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Subscription tiers for the SaaS platform.
 *
 * Each constant carries its own metadata (price, customer cap, feature flags)
 * so that the API, the landing pricing page, and the upgrade flow all read from
 * the same source of truth. The plan stored in the Tenant aggregate is just
 * the enum name; everything else is derived from here.
 */
public enum SubscriptionPlan {
    FREE(
        "Free",
        "Para empezar a probar el sistema.",
        new BigDecimal("0.00"),
        "soles",
        25,       // max customers
        50,       // max work orders / month
        0,        // max admin users
        false,    // whatsapp enabled?
        false,    // analytics enabled?
        false,    // priority support?
        new String[]{
            "Hasta 25 clientes",
            "Hasta 50 órdenes de trabajo / mes",
            "1 usuario administrador",
            "Soporte por comunidad (foro / email)"
        }
    ),
    BASIC(
        "Basic",
        "Para talleres que ya están en operación.",
        new BigDecimal("99.00"),
        "soles",
        200,
        1000,
        3,
        true,
        true,
        false,
        new String[]{
            "Hasta 200 clientes",
            "Hasta 1,000 órdenes / mes",
            "Hasta 3 usuarios administradores",
            "Notificaciones por WhatsApp (recordatorios)",
            "Dashboard de analítica con KPIs y charts",
            "Soporte por email en horario laboral"
        }
    ),
    PREMIUM(
        "Premium",
        "Para talleres con varias sucursales o alto volumen.",
        new BigDecimal("249.00"),
        "soles",
        Integer.MAX_VALUE,
        Integer.MAX_VALUE,
        15,
        true,
        true,
        true,
        new String[]{
            "Clientes ilimitados",
            "Órdenes ilimitadas",
            "Hasta 15 usuarios administradores",
            "WhatsApp: plantillas y respuestas automáticas",
            "Analítica avanzada + exportación a Excel",
            "Soporte prioritario 24/7 por WhatsApp y teléfono",
            "Reportes personalizados bajo pedido"
        }
    );

    private final String displayName;
    private final String tagline;
    private final BigDecimal monthlyPrice;
    private final String currency;
    private final int maxCustomers;
    private final int maxWorkOrdersPerMonth;
    private final int maxAdminUsers;
    private final boolean whatsappEnabled;
    private final boolean analyticsEnabled;
    private final boolean prioritySupport;
    private final String[] features;

    SubscriptionPlan(String displayName, String tagline, BigDecimal monthlyPrice,
                     String currency, int maxCustomers, int maxWorkOrdersPerMonth,
                     int maxAdminUsers, boolean whatsappEnabled, boolean analyticsEnabled,
                     boolean prioritySupport, String[] features) {
        this.displayName = displayName;
        this.tagline = tagline;
        this.monthlyPrice = monthlyPrice;
        this.currency = currency;
        this.maxCustomers = maxCustomers;
        this.maxWorkOrdersPerMonth = maxWorkOrdersPerMonth;
        this.maxAdminUsers = maxAdminUsers;
        this.whatsappEnabled = whatsappEnabled;
        this.analyticsEnabled = analyticsEnabled;
        this.prioritySupport = prioritySupport;
        this.features = features;
    }

    public String displayName() { return displayName; }
    public String tagline() { return tagline; }
    public BigDecimal monthlyPrice() { return monthlyPrice; }
    public String currency() { return currency; }
    public int maxCustomers() { return maxCustomers; }
    public int maxWorkOrdersPerMonth() { return maxWorkOrdersPerMonth; }
    public int maxAdminUsers() { return maxAdminUsers; }
    public boolean whatsappEnabled() { return whatsappEnabled; }
    public boolean analyticsEnabled() { return analyticsEnabled; }
    public boolean prioritySupport() { return prioritySupport; }
    public String[] features() { return features; }

    /**
     * Returns true if this plan is at least as feature-rich as `other`.
     * Used by the upgrade/downgrade flow to know whether a tenant can use
     * a given feature (e.g. "can this tenant send WhatsApp?").
     */
    public boolean isAtLeast(SubscriptionPlan other) {
        return this.ordinal() >= other.ordinal();
    }

    /** Sentinel value used in UI for "unlimited" caps. */
    public String displayCustomersLimit() {
        return maxCustomers == Integer.MAX_VALUE ? "Ilimitados" : String.valueOf(maxCustomers);
    }

    public String displayWorkOrdersLimit() {
        return maxWorkOrdersPerMonth == Integer.MAX_VALUE ? "Ilimitadas" : String.valueOf(maxWorkOrdersPerMonth);
    }
}
