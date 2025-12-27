package com.issuetracker.chatservice.client;

import com.issuetracker.chatservice.dto.response.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client to communicate with User Service
 */
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    
    /**
     * Validate JWT token and get user details
     */
    @PostMapping("/api/v1/auth/validate")
    TokenValidationResponse validateToken(@RequestHeader("Authorization") String token);
    
    /**
     * Get user by ID
     */
    @GetMapping("/api/v1/users/{userId}")
    TokenValidationResponse getUserById(@PathVariable("userId") String userId);
}