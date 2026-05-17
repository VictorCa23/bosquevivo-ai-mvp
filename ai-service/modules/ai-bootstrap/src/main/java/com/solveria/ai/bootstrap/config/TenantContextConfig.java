package com.solveria.ai.bootstrap.config;

import com.solveria.ai.application.port.out.TenantContextPort;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Tenant context configuration. Dev: fixed 'dev-tenant'. Test: 'test-tenant'. Later: JWT-based. */
@Configuration
public class TenantContextConfig {

    @Bean
    @Profile("dev")
    public TenantContextPort devTenantContextPort(
            @Value("${ai.tenant.dev.tenant-id:dev-tenant}") String tenantId,
            @Value("${ai.tenant.dev.principal:dev-user}") String principal) {
        return simple(tenantId, principal);
    }

    @Bean
    @Profile("test")
    public TenantContextPort testTenantContextPort() {
        return simple("test-tenant", "test-user");
    }

    private static TenantContextPort simple(String tenantId, String principal) {
        return new TenantContextPort() {
            @Override
            public String currentTenantId() {
                return tenantId;
            }

            @Override
            public String principal() {
                return principal;
            }

            @Override
            public List<String> scopes() {
                return List.of();
            }
        };
    }
}
