package com.issuetracker.chatservice.repository;

import com.issuetracker.chatservice.model.Conversation;
import com.issuetracker.chatservice.model.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Conversation entity
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    
    // Find conversation by user and department
    Optional<Conversation> findByUserIdAndDepartmentId(String userId, String departmentId);
    
    // Find all conversations for a user
    Page<Conversation> findByUserIdAndStatusOrderByLastMessageTimeDesc(
        String userId, ConversationStatus status, Pageable pageable);
    
    // Find all conversations for a user (any status)
    Page<Conversation> findByUserIdOrderByLastMessageTimeDesc(String userId, Pageable pageable);
    
    // Find all conversations for a department
    Page<Conversation> findByDepartmentIdAndStatusOrderByLastMessageTimeDesc(
        String departmentId, ConversationStatus status, Pageable pageable);
    
    // Find all conversations for a department (any status)
    Page<Conversation> findByDepartmentIdOrderByLastMessageTimeDesc(String departmentId, Pageable pageable);
    
    // Check if conversation exists
    boolean existsByUserIdAndDepartmentId(String userId, String departmentId);
    
    // Count active conversations for user
    long countByUserIdAndStatus(String userId, ConversationStatus status);
}
