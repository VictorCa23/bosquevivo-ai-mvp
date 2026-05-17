package com.solveria.ai.bootstrap.config;

import com.solveria.ai.application.port.out.AuditPort;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Audit port configuration. Stub: logs to SLF4J. */
@Configuration
public class AuditConfig {

    private static final Logger log = LoggerFactory.getLogger("com.solveria.ai.audit");

    @Bean
    public AuditPort loggerAuditPort() {
        return new AuditPort() {
            @Override
            public void audit(String event, Map<String, Object> details) {
                log.info("AUDIT {} {}", event, details);
            }
        };
    }
}
