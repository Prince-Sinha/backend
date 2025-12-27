package com.issuetracker.chatservice.model;

import com.issuetracker.chatservice.model.enums.MessageStatus;
import com.issuetracker.chatservice.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a single message in a conversation
 */
@Document(collection = "messages")
@CompoundIndexes({
    @CompoundIndex(name = "conv_time_idx", def = "{'conversationId': 1, 'sentAt': -1}"),
    @CompoundIndex(name = "sender_time_idx", def = "{'senderId': 1, 'sentAt': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    private String id;
    
    @Indexed
    private String conversationId;
    
    @Indexed
    private String senderId;         // User or Department ID who sent the message
    
    private String senderRole;       // NORMAL_USER or DEPARTMENT
    
    private String content;          // Message text content
    
    private MessageType type;        // TEXT, IMAGE, FILE, SYSTEM
    
    private MessageStatus status;    // SENT, DELIVERED, READ
    
    @Indexed
    private LocalDateTime sentAt;
    
    private LocalDateTime deliveredAt;
    
    private LocalDateTime readAt;
    
    // For message editing
    private boolean edited;
    private LocalDateTime editedAt;
    private String originalContent;  // Keep original content for audit
}
