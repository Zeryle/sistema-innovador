package com.utp.myapp.shared.infraestructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards every unmatched non-/api GET request to the Angular SPA (index.html).
 *
 * <p>Spring Boot's default static-resource handler serves the index.html at
 * {@code "/"}. Any Angular route (e.g. {@code /app/dashboard}, {@code /login})
 * would otherwise 404, because those files don't exist on disk. This controller
 * makes the SPA's client-side router take over for those paths.</p>
 *
 * <p>REST controllers (e.g. {@code /api/**}, {@code /api/webhook/**}) take
 * precedence, so this never intercepts API traffic. Static asset requests
 * (e.g. {@code /main-XXX.js}) are served by the classpath static resource
 * handler before they reach here.</p>
 */
@Controller
public class SpaForwardController {

    /**
     * SPA fallback. Accepts the common top-level Angular routes plus an
     * explicit {@code /app/**} catch-all for nested routes.
     */
    @GetMapping({
            "/app", "/app/**",
            "/login", "/register", "/pricing",
            "/billing", "/billing/**",
            "/dashboard", "/dashboard/**",
            "/customers", "/customers/**",
            "/vehicles", "/vehicles/**",
            "/work-orders", "/work-orders/**",
            "/reminders", "/reminders/**",
            "/whatsapp", "/whatsapp/**",
            "/analytics", "/analytics/**",
            "/settings", "/settings/**",
            "/checkout", "/checkout/**"
    })
    public String forwardSpa() {
        return "forward:/index.html";
    }
}