package com.issuetracker.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response for typing indicator notification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicatorResponse {
    
    private String conversationId;
    private String userId;
    private String userName;
    private boolean typing;
    private LocalDateTime timestamp;
}
