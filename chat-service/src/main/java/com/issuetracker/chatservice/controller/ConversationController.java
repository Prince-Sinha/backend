package com.issuetracker.chatservice.controller;

import com.issuetracker.chatservice.dto.request.CreateConversationRequest;
import com.issuetracker.chatservice.dto.response.ConversationResponse;
import com.issuetracker.chatservice.model.enums.ConversationStatus;
import com.issuetracker.chatservice.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for conversation management
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {
    
    private final ConversationService conversationService;
    
    /**
     * Create a new conversation
     */
    @PostMapping("/conversations")
    public ResponseEntity<Map<String, Object>> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("Authorization") String token) {
        
        log.info("Creating conversation request from user: {}", userId);
        
        // Remove "Bearer " prefix if present
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        ConversationResponse conversation = conversationService.createConversation(userId, request, cleanToken);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Conversation created successfully");
        response.put("data", conversation);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get user's conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> getUserConversations(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) ConversationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching conversations for user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationResponse> conversations = conversationService.getUserConversations(userId, status, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", conversations.getContent());
        response.put("currentPage", conversations.getNumber());
        response.put("totalItems", conversations.getTotalElements());
        response.put("totalPages", conversations.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get conversation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getConversation(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Fetching conversation: {} for user: {}", id, userId);
        
        ConversationResponse conversation = conversationService.getConversationById(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", conversation);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Close a conversation
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<Map<String, Object>> closeConversation(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Closing conversation: {} by user: {}", id, userId);
        
        ConversationResponse conversation = conversationService.closeConversation(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Conversation closed successfully");
        response.put("data", conversation);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get department conversations (for department users)
     */
    @GetMapping("/department")
    public ResponseEntity<Map<String, Object>> getDepartmentConversations(
            @RequestHeader("X-User-Id") String departmentId,
            @RequestParam(required = false) ConversationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching conversations for department: {}", departmentId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationResponse> conversations = conversationService.getDepartmentConversations(departmentId, status, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", conversations.getContent());
        response.put("currentPage", conversations.getNumber());
        response.put("totalItems", conversations.getTotalElements());
        response.put("totalPages", conversations.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
}