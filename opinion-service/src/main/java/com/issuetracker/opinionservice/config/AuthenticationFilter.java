package com.issuetracker.opinionservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuetracker.opinionservice.client.UserServiceClient;
import com.issuetracker.opinionservice.dto.ApiResponse;
import com.issuetracker.opinionservice.dto.TokenValidationResponse;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("Request: {} {}", method, path);

        // Skip authentication for CORS preflight requests (OPTIONS) - MUST BE FIRST!
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.info("OPTIONS preflight request - allowing for CORS");
            filterChain.doFilter(request, response);
            return;
        }

        // Allow GET requests (public - no authentication required)
        if ("GET".equalsIgnoreCase(method)) {
            log.info("GET request - allowing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        // Allow WebSocket connections
        if (path.contains("/ws/")) {
            log.info("WebSocket request - allowing");
            filterChain.doFilter(request, response);
            return;
        }

        // Allow Swagger/OpenAPI docs
        if (path.contains("/swagger") || path.contains("/api-docs") || path.contains("/actuator")) {
            log.info("Documentation/Actuator request - allowing");
            filterChain.doFilter(request, response);
            return;
        }

        // For POST, PUT, DELETE - require authentication
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            log.info("{} request - authentication required", method);

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
                return;
            }

            try {
                // Validate token with User Service
                TokenValidationResponse validationResponse = userServiceClient.validateToken(authHeader);

                if (!validationResponse.isValid()) {
                    log.warn("Invalid token: {}", validationResponse.getMessage());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, validationResponse.getMessage());
                    return;
                }

                // Token is valid - set userId as request attribute for use in controllers
                request.setAttribute("userId", validationResponse.getUserId());
                log.info("Token validated successfully for user: {}", validationResponse.getUserId());

                // Proceed with request - let business logic errors propagate normally
                filterChain.doFilter(request, response);

            } catch (FeignException.Unauthorized e) {
                log.error("Token validation failed - Unauthorized");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            } catch (FeignException e) {
                log.error("Error communicating with User Service: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Authentication service unavailable");
            } catch (ServletException | IOException e) {
                // Let ServletException and IOException propagate - these are from filterChain
                // and should be handled by Spring's error handling
                throw e;
            }
        } else {
            // Allow other methods (OPTIONS, HEAD, etc.)
            filterChain.doFilter(request, response);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .status("error")
                .message(message)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}