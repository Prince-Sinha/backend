package com.issuetracker.chatservice.controller;

import com.issuetracker.chatservice.dto.request.SendMessageRequest;
import com.issuetracker.chatservice.dto.response.MessageResponse;
import com.issuetracker.chatservice.service.ConversationService;
import com.issuetracker.chatservice.service.MessageService;
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
 * REST controller for message management
 */
@RestController
@RequestMapping("/api/v1/chat/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    
    private final MessageService messageService;
    private final ConversationService conversationService;
    
    /**
     * Send a message
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Sending message from user: {}", userId);
        
        MessageResponse message = messageService.sendMessage(userId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Message sent successfully");
        response.put("data", message);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get messages in a conversation
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Fetching messages for conversation: {}", conversationId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageResponse> messages = messageService.getMessages(conversationId, userId, pageable);
        
        // Reset unread count when user opens conversation
        conversationService.resetUnreadCount(conversationId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", messages.getContent());
        response.put("currentPage", messages.getNumber());
        response.put("totalItems", messages.getTotalElements());
        response.put("totalPages", messages.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark message as read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Marking message as read: {}", id);
        
        MessageResponse message = messageService.markAsRead(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Message marked as read");
        response.put("data", message);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Edit a message
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editMessage(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Editing message: {}", id);
        
        String newContent = body.get("content");
        MessageResponse message = messageService.editMessage(id, newContent, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Message edited successfully");
        response.put("data", message);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete a message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("Deleting message: {}", id);
        
        messageService.deleteMessage(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Message deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}