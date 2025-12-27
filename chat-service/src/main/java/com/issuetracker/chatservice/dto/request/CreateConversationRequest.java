package com.issuetracker.chatservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to create a new conversation between user and department
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    
    @NotBlank(message = "Department ID is required")
    private String departmentId;
    
    private String initialMessage;  // Optional initial message to start conversation
}