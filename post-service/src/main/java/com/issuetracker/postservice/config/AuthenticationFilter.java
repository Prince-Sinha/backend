
package com.issuetracker.postservice.config;

import com.issuetracker.postservice.client.UserServiceClient;
import com.issuetracker.postservice.dto.ApiResponse;
import feign.FeignException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements Filter {

    private final UserServiceClient userServiceClient;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        log.info("Incoming request: {} {}", method, path);

        // Skip authentication for CORS preflight requests (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.info("Skipping authentication for CORS preflight request: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Skip authentication for health checks and actuator endpoints
        if (path.startsWith("/actuator") || path.startsWith("/swagger") || path.startsWith("/api-docs")) {
            log.info("Skipping authentication for: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Skip authentication for GET requests (read-only operations)
        if ("GET".equalsIgnoreCase(method)) {
            log.info("Skipping authentication for GET request: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Get Authorization header
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || authHeader.trim().isEmpty()) {
            log.error("Missing Authorization header for: {}", path);
            sendUnauthorizedResponse(httpResponse, "Missing Authorization header");
            return;
        }

        // Validate token with User Service
        try {
            log.info("Validating token with User Service...");
            ApiResponse<Map<String, Object>> validationResponse = userServiceClient.validateToken(authHeader);

            if ("success".equals(validationResponse.getStatus()) && validationResponse.getData() != null) {
                Map<String, Object> userInfo = validationResponse.getData();
                String userId = (String) userInfo.get("userId");
                String role = (String) userInfo.get("role");

                log.info("✅ Token validated successfully - userId: {}, role: {}", userId, role);

                // Add user info to request attributes for use in controllers
                httpRequest.setAttribute("userId", userId);
                httpRequest.setAttribute("userRole", role);
                httpRequest.setAttribute("userEmail", userInfo.get("email"));

                // Continue with the request
                chain.doFilter(request, response);
            } else {
                log.error("Token validation failed: {}", validationResponse.getMessage());
                sendUnauthorizedResponse(httpResponse, "Invalid token");
            }

        } catch (FeignException.Unauthorized e) {
            log.error("Unauthorized: {}", e.getMessage());
            sendUnauthorizedResponse(httpResponse, "Unauthorized - Invalid or expired token");
        } catch (FeignException e) {
            log.error("Error calling User Service for token validation: {}", e.getMessage());
            sendUnauthorizedResponse(httpResponse, "Authentication service unavailable");
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage(), e);
            sendUnauthorizedResponse(httpResponse, "Authentication failed");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
                java.time.LocalDateTime.now(),
                message
        ));
    }
}
