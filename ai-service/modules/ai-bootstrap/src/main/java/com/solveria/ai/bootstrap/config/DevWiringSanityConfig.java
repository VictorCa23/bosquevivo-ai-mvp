package com.solveria.ai.bootstrap.config;

import com.solveria.ai.application.port.out.LlmChatPort;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Development wiring sanity check configuration. Logs registered LlmChatPort beans to verify
 * correct wiring in dev profile.
 */
@Configuration
@Profile("dev")
public class DevWiringSanityConfig {

    private static final Logger log = LoggerFactory.getLogger(DevWiringSanityConfig.class);

    @Bean
    public ApplicationRunner llmChatPortSanityCheck(ApplicationContext applicationContext) {
        return args -> {
            Map<String, LlmChatPort> beans = applicationContext.getBeansOfType(LlmChatPort.class);
            log.info("=== LlmChatPort Wiring Sanity Check ===");
            log.info("Found {} LlmChatPort bean(s):", beans.size());
            beans.forEach(
                    (name, bean) -> {
                        log.info("  - Bean name: '{}', Type: {}", name, bean.getClass().getName());
                    });
            if (beans.isEmpty()) {
                log.warn("WARNING: No LlmChatPort beans found! This may cause startup failures.");
            } else {
                log.info("LlmChatPort wiring OK - {} bean(s) registered", beans.size());
            }
            log.info("=======================================");
        };
    }
}
