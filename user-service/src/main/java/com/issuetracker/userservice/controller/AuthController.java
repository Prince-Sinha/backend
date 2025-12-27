
package com.issuetracker.userservice.controller;

import com.issuetracker.userservice.dto.ApiResponse;
import com.issuetracker.userservice.dto.AuthResponse;
import com.issuetracker.userservice.dto.LoginAdminRequest;
import com.issuetracker.userservice.dto.LoginRequest;
import com.issuetracker.userservice.dto.SignupRequest;
import com.issuetracker.userservice.security.JwtService;
import com.issuetracker.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user with phone number")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/loginAdmin")
    @Operation(summary = "Login admin with email")
    public ResponseEntity<AuthResponse> loginAdmin(@Valid @RequestBody LoginAdminRequest request) {
        AuthResponse response = authService.loginAdmin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token - called by other microservices")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.<Map<String, Object>>builder()
                                .status("error")
                                .message("Missing or invalid Authorization header")
                                .build());
            }

            String token = authHeader.substring(7);
            
            // Extract claims from token
            String email = jwtService.extractEmail(token);
            String userId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
            String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
            
            // Check if token is expired
            if (jwtService.extractClaim(token, claims -> claims.getExpiration()).before(new java.util.Date())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.<Map<String, Object>>builder()
                                .status("error")
                                .message("Token expired")
                                .build());
            }

            // Token is valid - return user info
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userId);
            userInfo.put("email", email);
            userInfo.put("role", role);
            userInfo.put("valid", true);

            log.info("Token validated successfully for user: {}", email);

            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .status("success")
                    .message("Token is valid")
                    .data(userInfo)
                    .build());

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .status("error")
                            .message("Invalid token: " + e.getMessage())
                            .build());
        }
    }
}
