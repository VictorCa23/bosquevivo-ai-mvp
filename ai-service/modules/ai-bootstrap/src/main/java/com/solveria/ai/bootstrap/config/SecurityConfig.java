package com.solveria.ai.bootstrap.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    @ConditionalOnProperty(name = "security.jwt.enabled", havingValue = "true")
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("event=AI_SECURITY_CONFIG_JWT_ENABLED");

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        a -> {
                            a.requestMatchers("/actuator/health/**", "/actuator/info/**")
                                    .permitAll();
                            a.requestMatchers(
                                            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                    .permitAll();
                            a.anyRequest().authenticated();
                        })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        e ->
                                e.authenticationEntryPoint(
                                                new BearerTokenAuthenticationEntryPoint())
                                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(
            name = "security.jwt.enabled",
            havingValue = "false",
            matchIfMissing = true)
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("event=AI_SECURITY_CONFIG_JWT_DISABLED");

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
