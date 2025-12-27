package com.issuetracker.opinionservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenValidationResponse {
    private String status;
    private String message;
    private Map<String, Object> data;
    
    // Helper methods
    public boolean isValid() {
        return "success".equalsIgnoreCase(status) && data != null && 
               Boolean.TRUE.equals(data.get("valid"));
    }
    
    public String getUserId() {
        return data != null ? (String) data.get("userId") : null;
    }
    
    public String getEmail() {
        return data != null ? (String) data.get("email") : null;
    }
    
    public String getRole() {
        return data != null ? (String) data.get("role") : null;
    }
}