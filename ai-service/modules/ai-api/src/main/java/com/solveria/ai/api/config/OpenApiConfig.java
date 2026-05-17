package com.solveria.ai.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/** OpenAPI configuration for AI Service. */
@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "AI Service API",
                        version = "v1",
                        description = "AI completion and vector search API."))
public class OpenApiConfig {}
