package com.utp.myapp.shared.infraestructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards every unmatched non-/api GET request to the Angular SPA (index.html).
 *
 * Spring Boot's default static-resource handler serves the index.html at "/".
 * Any Angular route (e.g. /app/dashboard, /login) would otherwise 404, because
 * those files don't exist on disk. This controller makes the SPA's client-side
 * router take over for those paths.
 *
 * Note: REST controllers (e.g. /api/**, /api/webhook/**) take precedence, so this
 * never intercepts API traffic. Static asset requests (e.g. /main-XXX.js) are
 * served by the classpath:/static/ resource handler before they reach here.
 */
@Controller
public class SpaForwardController {

    @GetMapping({"/app/**", "/login", "/register", "/pricing",
                 "/billing/**", "/error"})
    public String forwardSpa() {
        return "forward:/index.html";
    }
}
