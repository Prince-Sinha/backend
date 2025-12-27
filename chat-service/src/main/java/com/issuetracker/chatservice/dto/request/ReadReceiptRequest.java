package com.issuetracker.chatservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to mark a message as read
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptRequest {
    
    @NotBlank(message = "Message ID is required")
    private String messageId;
    
    @NotBlank(message = "Conversation ID is required")
    private String conversationId;
}
