package com.utp.myapp.shared.interfaces.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration for AutoTaller SaaS.
 * <p>
 * Exposes:
 * <ul>
 *   <li>GET /v3/api-docs   — raw OpenAPI 3.0 JSON spec</li>
 *   <li>GET /swagger-ui/index.html — interactive Swagger UI</li>
 * </ul>
 * Adds a JWT bearer security scheme so the "Authorize" button works.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI autotallerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoTaller SaaS API")
                        .description("REST API for AutoTaller — workshop management SaaS. "
                                + "13 bounded contexts: analytics, auth, billing, catalog, "
                                + "publicapi, reminder, sales, shared, tenant, vehicle, "
                                + "whatsapp, workorder.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AutoTaller Team")
                                .url("https://github.com/Zeryle/sistema-innovador"))
                        .license(new License()
                                .name("Proyecto académico — UTP DWI")
                                .url("https://github.com/Zeryle/sistema-innovador")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste a JWT obtained from /api/auth/login")));
    }
}