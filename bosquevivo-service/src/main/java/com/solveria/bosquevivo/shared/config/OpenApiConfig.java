package com.solveria.bosquevivo.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "BosqueVivo AI API",
                        version = "0.7.1",
                        description =
                                "MVP 0.7.1 API with Core Platform and AI Service integration"))
public class OpenApiConfig {}
