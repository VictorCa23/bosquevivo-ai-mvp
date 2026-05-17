package com.solveria.ai.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Security configuration for JWT Bearer authentication. Defines the security scheme for
 * Swagger UI "Authorize" button and applies it globally.
 */
@Configuration
public class OpenApiSecurityConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String schemeName = "bearerAuth";

        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        schemeName,
                                        new SecurityScheme()
                                                .name(schemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "JWT token from IAM (e.g. Keycloak). Use Authorize to set Bearer token.")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
