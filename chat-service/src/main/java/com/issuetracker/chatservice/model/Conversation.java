package com.issuetracker.chatservice.model;

import com.issuetracker.chatservice.model.enums.ConversationStatus;
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
 * Represents a conversation between a normal user and a department
 * Business Rule: Only NORMAL_USER <-> DEPARTMENT conversations are allowed
 */
@Document(collection = "conversations")
@CompoundIndexes({
    @CompoundIndex(name = "user_dept_idx", def = "{'userId': 1, 'departmentId': 1}"),
    @CompoundIndex(name = "user_status_time_idx", def = "{'userId': 1, 'status': 1, 'lastMessageTime': -1}"),
    @CompoundIndex(name = "dept_status_time_idx", def = "{'departmentId': 1, 'status': 1, 'lastMessageTime': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;           // Normal user ID
    
    @Indexed
    private String departmentId;     // Department user ID
    
    // Metadata about last message for quick display
    private String lastMessage;      // Preview of last message
    private LocalDateTime lastMessageTime;
    private String lastMessageSenderId;
    
    // Unread message counts for both parties
    private int unreadCountUser;     // Unread messages for normal user
    private int unreadCountDept;     // Unread messages for department
    
    @Indexed
    private ConversationStatus status; // ACTIVE, CLOSED, ARCHIVED
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}