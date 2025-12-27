package com.issuetracker.chatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Chat Service
 * Allows WebSocket connections while protecting REST API endpoints
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for WebSocket
            .authorizeHttpRequests(auth -> auth
                // Allow WebSocket handshake endpoints
                .requestMatchers("/chat-websocket/**").permitAll()
                // Allow actuator health checks
                .requestMatchers("/actuator/**").permitAll()
                // All other requests require authentication (handled by custom filter)
                .anyRequest().permitAll() // Temporary: allow all for testing
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .httpBasic(httpBasic -> httpBasic.disable()) // Disable basic auth popup
            .formLogin(form -> form.disable()); // Disable form login

        return http.build();
    }
}