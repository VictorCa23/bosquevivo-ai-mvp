package com.solveria.iamservice.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT security.
 *
 * <p>When enabled=true, the service requires JWT authentication for /api/** endpoints. When
 * enabled=false (default), all endpoints are accessible without authentication (DEV mode).
 *
 * <p>JWT issuer/jwk configuration is handled by Spring's standard properties: -
 * spring.security.oauth2.resourceserver.jwt.issuer-uri -
 * spring.security.oauth2.resourceserver.jwt.jwk-set-uri
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        /** Enable JWT authentication. Default: false (DEV mode without auth). */
        boolean enabled, String secret, long expirationMinutes) {}
