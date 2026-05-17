package com.solveria.iamservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for IAM Service.
 *
 * <p>Defines API documentation including JWT Bearer authentication scheme. When
 * security.jwt.enabled=true, endpoints under /api/** require Bearer JWT token.
 */
@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "SolverIA IAM API",
                        version = "v1",
                        description =
                                "Identity and Access Management API. "
                                        + "When JWT security is enabled (security.jwt.enabled=true), "
                                        + "all endpoints under /api/** require Bearer JWT authentication.",
                        contact =
                                @Contact(
                                        name = "SolverIA Platform Team",
                                        email = "platform-team@solveria.com"),
                        license =
                                @License(
                                        name = "Proprietary",
                                        url = "https://www.solveria.com/license")),
        tags = {
            @Tag(name = "Roles", description = "Role management operations"),
            @Tag(name = "Permissions", description = "Permission management operations")
        })
@SecurityScheme(
        name = "bearer-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description =
                "JWT Bearer token authentication. "
                        + "Obtain token from your identity provider and include it in the Authorization header: "
                        + "Authorization: Bearer <JWT_TOKEN>")
public class OpenApiConfig {}
