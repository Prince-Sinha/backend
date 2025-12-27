package com.issuetracker.chatservice.dto.response;

import com.issuetracker.chatservice.model.enums.ConversationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response containing conversation details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    
    private String id;
    private String userId;
    private String userName;
    private String departmentId;
    private String departmentName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String lastMessageSenderId;
    private int unreadCount;  // Unread count for the requester
    private ConversationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

