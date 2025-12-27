package com.issuetracker.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from User Service for token validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    
    private String status;  // user-service returns "status": "success"
    private String message;
    private UserData data;
    
    // Helper method to check if token is valid
    public boolean isSuccess() {
        return "success".equals(status);
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {
        private String userId;
        private String name;
        private String email;
        private String role;
    }
}
