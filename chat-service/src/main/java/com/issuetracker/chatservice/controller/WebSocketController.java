package com.issuetracker.chatservice.controller;

import com.issuetracker.chatservice.dto.request.ReadReceiptRequest;
import com.issuetracker.chatservice.dto.request.SendMessageRequest;
import com.issuetracker.chatservice.dto.request.TypingIndicatorRequest;
import com.issuetracker.chatservice.dto.response.MessageResponse;
import com.issuetracker.chatservice.dto.response.ReadReceiptResponse;
import com.issuetracker.chatservice.dto.response.TypingIndicatorResponse;
import com.issuetracker.chatservice.service.MessageService;
import com.issuetracker.chatservice.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * WebSocket controller for real-time chat
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final MessageService messageService;
    private final WebSocketService webSocketService;
    
    /**
     * Handle sending messages via WebSocket
     */
    @MessageMapping("/chat/send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        log.info("WebSocket message received from: {}", principal.getName());
        
        try {
            // Send message and save to database
            MessageResponse message = messageService.sendMessage(principal.getName(), request);
            
            // Broadcast to conversation participants
            webSocketService.sendMessageToConversation(request.getConversationId(), message);
            
        } catch (Exception e) {
            log.error("Error sending message via WebSocket: {}", e.getMessage());
        }
    }
    
    /**
     * Handle typing indicators
     */
    @MessageMapping("/chat/typing")
    public void handleTypingIndicator(@Payload TypingIndicatorRequest request, Principal principal) {
        log.debug("Typing indicator from: {}", principal.getName());
        
        TypingIndicatorResponse indicator = TypingIndicatorResponse.builder()
                .conversationId(request.getConversationId())
                .userId(principal.getName())
                .userName(principal.getName())
                .typing(request.getTyping())
                .timestamp(LocalDateTime.now())
                .build();
        
        webSocketService.sendTypingIndicator(request.getConversationId(), indicator);
    }
    
    /**
     * Handle read receipts
     */
    @MessageMapping("/chat/read")
    public void handleReadReceipt(@Payload ReadReceiptRequest request, Principal principal) {
        log.debug("Read receipt for message: {}", request.getMessageId());
        
        try {
            MessageResponse message = messageService.markAsRead(request.getMessageId(), principal.getName());
            
            ReadReceiptResponse receipt = ReadReceiptResponse.builder()
                    .messageId(request.getMessageId())
                    .conversationId(request.getConversationId())
                    .readBy(principal.getName())
                    .readByName(principal.getName())
                    .readAt(message.getReadAt())
                    .build();
            
            webSocketService.sendReadReceipt(request.getConversationId(), receipt);
            
        } catch (Exception e) {
            log.error("Error handling read receipt: {}", e.getMessage());
        }
    }
}
