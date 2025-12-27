package com.issuetracker.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response for read receipt notification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptResponse {
    
    private String messageId;
    private String conversationId;
    private String readBy;
    private String readByName;
    private LocalDateTime readAt;
}
