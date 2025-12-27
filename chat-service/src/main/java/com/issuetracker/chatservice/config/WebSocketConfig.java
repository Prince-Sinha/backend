package com.issuetracker.chatservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for STOMP messaging
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    
    @Value("${websocket.endpoint:/chat-websocket}")
    private String websocketEndpoint;
    
    @Value("${websocket.message-broker.application-prefix:/app}")
    private String applicationPrefix;
    
    @Value("${websocket.message-broker.user-prefix:/user}")
    private String userPrefix;
    
    @Value("${websocket.message-broker.topic-prefix:/topic}")
    private String topicPrefix;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for pub/sub messaging
        config.enableSimpleBroker(topicPrefix, userPrefix);
        
        // Set application destination prefix
        config.setApplicationDestinationPrefixes(applicationPrefix);
        
        // Set user destination prefix
        config.setUserDestinationPrefix(userPrefix);
    }
    
    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        // Register authentication interceptor
        registration.interceptors(webSocketAuthInterceptor);
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint
        registry.addEndpoint(websocketEndpoint)
                .setAllowedOriginPatterns("*")  // Allow all origins for testing (file:// protocol support)
                .withSockJS();
    }
}
