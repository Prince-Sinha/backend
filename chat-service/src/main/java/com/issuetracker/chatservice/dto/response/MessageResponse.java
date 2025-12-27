package com.issuetracker.chatservice.dto.response;

import com.issuetracker.chatservice.model.enums.MessageStatus;
import com.issuetracker.chatservice.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response containing message details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private String id;
    private String conversationId;
    private String senderId;
    private String senderRole;
    private String senderName;
    private String content;
    private MessageType type;
    private MessageStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private boolean edited;
    private LocalDateTime editedAt;
}