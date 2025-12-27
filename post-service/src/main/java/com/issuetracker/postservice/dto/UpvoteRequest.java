package com.issuetracker.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpvoteRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
}
