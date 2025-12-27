package com.issuetracker.chatservice.config;

import com.issuetracker.chatservice.client.UserServiceClient;
import com.issuetracker.chatservice.dto.response.TokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for JWT authentication
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    
    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip authentication for health check and websocket handshake
        if (path.contains("/actuator") || path.contains("/chat-websocket")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Missing or invalid authorization header\"}");
            return;
        }
        
        try {
            // Validate token with User Service
            TokenValidationResponse validation = userServiceClient.validateToken(authHeader);
            
            if (!validation.isSuccess() || validation.getData() == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\":false,\"message\":\"Invalid token\"}");
                return;
            }
            
            // Add user ID to request header
            request.setAttribute("X-User-Id", validation.getData().getUserId());
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Authentication failed\"}");
        }
    }
}