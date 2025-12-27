package com.issuetracker.chatservice.service;

import com.issuetracker.chatservice.client.UserServiceClient;
import com.issuetracker.chatservice.dto.request.CreateConversationRequest;
import com.issuetracker.chatservice.dto.response.ConversationResponse;
import com.issuetracker.chatservice.dto.response.TokenValidationResponse;
import com.issuetracker.chatservice.exception.BadRequestException;
import com.issuetracker.chatservice.exception.ConversationNotFoundException;
import com.issuetracker.chatservice.exception.UnauthorizedChatException;
import com.issuetracker.chatservice.model.Conversation;
import com.issuetracker.chatservice.model.enums.ConversationStatus;
import com.issuetracker.chatservice.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for managing conversations between users and departments
 */
@Service
@Slf4j
public class ConversationService {
    
    private final ConversationRepository conversationRepository;
    private final UserServiceClient userServiceClient;
    private final MessageService messageService;
    
    // Constructor with @Lazy to break circular dependency
    public ConversationService(
            ConversationRepository conversationRepository,
            UserServiceClient userServiceClient,
            @Lazy MessageService messageService) {
        this.conversationRepository = conversationRepository;
        this.userServiceClient = userServiceClient;
        this.messageService = messageService;
    }
    
    @Value("${conversation.max-per-user:50}")
    private int maxConversationsPerUser;
    
    /**
     * Create a new conversation between user and department
     * Business Rule: Only NORMAL_USER can initiate chat with DEPARTMENT
     */
    @Transactional
    public ConversationResponse createConversation(String userId, CreateConversationRequest request, String token) {
        log.info("Creating conversation between user {} and department {}", userId, request.getDepartmentId());
        
        // Validate users exist and get their roles
        TokenValidationResponse userValidation = userServiceClient.validateToken("Bearer " + token);
        System.out.println("Problem is here 1");
        if (!userValidation.isSuccess() || userValidation.getData() == null) {
            throw new UnauthorizedChatException("Invalid user token");
        }
         System.out.println("Problem is here 2");
        
        String userRole = userValidation.getData().getRole();
        System.out.println("Problem is here 3");
        // Check if conversation already exists
        if (conversationRepository.existsByUserIdAndDepartmentId(userId, request.getDepartmentId())) {
            throw new BadRequestException("Conversation already exists between these users");
        }

        System.out.println("Problem is here 3");
        
        // Check conversation limit
        long activeConversations = conversationRepository.countByUserIdAndStatus(userId, ConversationStatus.ACTIVE);
        if (activeConversations >= maxConversationsPerUser) {
            throw new BadRequestException("Maximum conversation limit reached");
        }
        System.out.println("Problem is here 4");
        
        // Create conversation
        Conversation conversation = Conversation.builder()
                .userId(userId)
                .departmentId(request.getDepartmentId())
                .status(ConversationStatus.ACTIVE)
                .unreadCountUser(0)
                .unreadCountDept(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
                System.out.println("Problem is here 5");
        conversation = conversationRepository.save(conversation);
        System.out.println("Problem is here 6");
        
        // Save initial message if provided
        if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
            log.info("Saving initial message for conversation: {}", conversation.getId());
            com.issuetracker.chatservice.dto.request.SendMessageRequest messageRequest = 
                com.issuetracker.chatservice.dto.request.SendMessageRequest.builder()
                    .conversationId(conversation.getId())
                    .content(request.getInitialMessage())
                    .type(com.issuetracker.chatservice.model.enums.MessageType.TEXT)
                    .build();
            messageService.sendMessage(userId, messageRequest);
        }
        
        log.info("Conversation created successfully: {}", conversation.getId());
        return mapToResponse(conversation, userId);
    }
    
    /**
     * Get all conversations for a user
     */
    public Page<ConversationResponse> getUserConversations(String userId, ConversationStatus status, Pageable pageable) {
        Page<Conversation> conversations;
        
        if (status != null) {
            conversations = conversationRepository.findByUserIdAndStatusOrderByLastMessageTimeDesc(userId, status, pageable);
        } else {
            conversations = conversationRepository.findByUserIdOrderByLastMessageTimeDesc(userId, pageable);
        }
        
        return conversations.map(conv -> mapToResponse(conv, userId));
    }
    
    /**
     * Get all conversations for a department
     */
    public Page<ConversationResponse> getDepartmentConversations(String departmentId, ConversationStatus status, Pageable pageable) {
        Page<Conversation> conversations;
        
        if (status != null) {
            conversations = conversationRepository.findByDepartmentIdAndStatusOrderByLastMessageTimeDesc(departmentId, status, pageable);
        } else {
            conversations = conversationRepository.findByDepartmentIdOrderByLastMessageTimeDesc(departmentId, pageable);
        }
        
        return conversations.map(conv -> mapToResponse(conv, departmentId));
    }
    
    /**
     * Get conversation by ID
     */
    public ConversationResponse getConversationById(String id, String requesterId) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found: " + id));
        
        // Verify requester is participant
        if (!conversation.getUserId().equals(requesterId) && !conversation.getDepartmentId().equals(requesterId)) {
            throw new UnauthorizedChatException("You are not a participant in this conversation");
        }
        
        return mapToResponse(conversation, requesterId);
    }
    
    /**
     * Close a conversation
     */
    @Transactional
    public ConversationResponse closeConversation(String id, String requesterId) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found: " + id));
        
        // Verify requester is participant
        if (!conversation.getUserId().equals(requesterId) && !conversation.getDepartmentId().equals(requesterId)) {
            throw new UnauthorizedChatException("You are not a participant in this conversation");
        }
        
        conversation.setStatus(ConversationStatus.CLOSED);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        conversation = conversationRepository.save(conversation);
        log.info("Conversation closed: {}", id);
        
        return mapToResponse(conversation, requesterId);
    }
    
    /**
     * Update last message metadata
     */
    @Transactional
    public void updateLastMessage(String conversationId, String messageContent, String senderId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found: " + conversationId));
        
        conversation.setLastMessage(messageContent.length() > 100 ? messageContent.substring(0, 100) + "..." : messageContent);
        conversation.setLastMessageTime(LocalDateTime.now());
        conversation.setLastMessageSenderId(senderId);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        // Increment unread count for recipient
        if (senderId.equals(conversation.getUserId())) {
            conversation.setUnreadCountDept(conversation.getUnreadCountDept() + 1);
        } else {
            conversation.setUnreadCountUser(conversation.getUnreadCountUser() + 1);
        }
        
        conversationRepository.save(conversation);
    }
    
    /**
     * Reset unread count for a user
     */
    @Transactional
    public void resetUnreadCount(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found: " + conversationId));
        
        if (userId.equals(conversation.getUserId())) {
            conversation.setUnreadCountUser(0);
        } else if (userId.equals(conversation.getDepartmentId())) {
            conversation.setUnreadCountDept(0);
        }
        
        conversationRepository.save(conversation);
    }
    
    private ConversationResponse mapToResponse(Conversation conversation, String requesterId) {
        int unreadCount = requesterId.equals(conversation.getUserId()) ? 
                conversation.getUnreadCountUser() : conversation.getUnreadCountDept();
        
        // Fetch user and department names from user-service
        String userName = "User";
        String departmentName = "Department";
        
        try {
            // Fetch user name
            TokenValidationResponse userResponse = userServiceClient.getUserById(conversation.getUserId());
            if (userResponse != null && userResponse.getData() != null) {
                userName = userResponse.getData().getName();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user name for user: {}", conversation.getUserId());
        }
        
        try {
            // Fetch department name
            TokenValidationResponse deptResponse = userServiceClient.getUserById(conversation.getDepartmentId());
            if (deptResponse != null && deptResponse.getData() != null) {
                departmentName = deptResponse.getData().getName();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch department name for dept: {}", conversation.getDepartmentId());
        }
        
        return ConversationResponse.builder()
                .id(conversation.getId())
                .userId(conversation.getUserId())
                .userName(userName)
                .departmentId(conversation.getDepartmentId())
                .departmentName(departmentName)
                .lastMessage(conversation.getLastMessage())
                .lastMessageTime(conversation.getLastMessageTime())
                .lastMessageSenderId(conversation.getLastMessageSenderId())
                .unreadCount(unreadCount)
                .status(conversation.getStatus())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}
