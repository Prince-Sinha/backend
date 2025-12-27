package com.issuetracker.chatservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to send typing indicator
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicatorRequest {
    
    @NotBlank(message = "Conversation ID is required")
    private String conversationId;
    
    @NotNull(message = "Typing status is required")
    private Boolean typing;
}
