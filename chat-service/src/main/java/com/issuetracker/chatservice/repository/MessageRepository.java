package com.issuetracker.chatservice.repository;

import com.issuetracker.chatservice.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Message entity
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    // Find all messages in a conversation with pagination
    Page<Message> findByConversationIdOrderBySentAtDesc(String conversationId, Pageable pageable);
    
    // Find all messages in a conversation (no pagination)
    List<Message> findByConversationIdOrderBySentAtAsc(String conversationId);
    
    // Find messages by sender
    Page<Message> findBySenderIdOrderBySentAtDesc(String senderId, Pageable pageable);
    
    // Count unread messages in a conversation for a specific user
    long countByConversationIdAndSenderIdNotAndReadAtIsNull(String conversationId, String userId);
    
    // Delete all messages in a conversation
    void deleteByConversationId(String conversationId);
}