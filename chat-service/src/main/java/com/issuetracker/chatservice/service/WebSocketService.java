package com.issuetracker.chatservice.service;

import com.issuetracker.chatservice.dto.response.MessageResponse;
import com.issuetracker.chatservice.dto.response.ReadReceiptResponse;
import com.issuetracker.chatservice.dto.response.TypingIndicatorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for WebSocket real-time messaging
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
 
 private final SimpMessagingTemplate messagingTemplate;
 
 /**
 * Send message to a specific user
 */
 public void sendMessageToUser(String userId, MessageResponse message) {
 log.debug("Sending message to user: {}", userId);
 messagingTemplate.convertAndSendToUser(userId, "/queue/messages", message);
 }
 
 /**
 * Broadcast message to all participants in a conversation
 */
 public void sendMessageToConversation(String conversationId, MessageResponse message) {
 log.debug("Broadcasting message to conversation: {}", conversationId);
 messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/messages", message);
 }
 
 /**
 * Send typing indicator
 */
 public void sendTypingIndicator(String conversationId, TypingIndicatorResponse indicator) {
 log.debug("Sending typing indicator for conversation: {}", conversationId);
 messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/typing", indicator);
 }
 
 /**
 * Send read receipt
 */
 public void sendReadReceipt(String conversationId, ReadReceiptResponse receipt) {
 log.debug("Sending read receipt for conversation: {}", conversationId);
 messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/status", receipt);
 }
 
 /**
 * Notify department of new conversation
 */
 public void notifyDepartment(String departmentId, Object notification) {
 log.debug("Notifying department: {}", departmentId);
 messagingTemplate.convertAndSend("/topic/department/" + departmentId + "/conversations", notification);
 }
}
