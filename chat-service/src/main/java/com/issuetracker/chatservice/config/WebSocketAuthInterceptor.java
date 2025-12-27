package com.issuetracker.chatservice.config;

import com.issuetracker.chatservice.client.UserServiceClient;
import com.issuetracker.chatservice.dto.response.TokenValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Interceptor to authenticate WebSocket connections using JWT
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    
    private final UserServiceClient userServiceClient;
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("WebSocket CONNECT command received");
            
            // Extract Authorization header
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                log.info("Authorization header found: {}", authHeader.substring(0, Math.min(20, authHeader.length())) + "...");
                
                try {
                    // Validate token with user-service
                    TokenValidationResponse validation = userServiceClient.validateToken(authHeader);
                    
                    if (validation.isSuccess() && validation.getData() != null) {
                        String userId = validation.getData().getUserId();
                        log.info("WebSocket authentication successful for user: {}", userId);
                        
                        // Set user as Principal
                        Principal principal = () -> userId;
                        accessor.setUser(principal);
                        
                        // Also set in security context
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.warn("Token validation failed: {}", validation.getMessage());
                        throw new IllegalArgumentException("Invalid token");
                    }
                } catch (Exception e) {
                    log.error("WebSocket authentication error: {}", e.getMessage());
                    throw new IllegalArgumentException("Authentication failed: " + e.getMessage());
                }
            } else {
                log.warn("No Authorization header found in WebSocket connection");
                throw new IllegalArgumentException("Missing Authorization header");
            }
        }
        
        return message;
    }
}



