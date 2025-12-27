package com.issuetracker.chatservice.service;

import com.issuetracker.chatservice.dto.request.SendMessageRequest;
import com.issuetracker.chatservice.dto.response.MessageResponse;
import com.issuetracker.chatservice.exception.ConversationNotFoundException;
import com.issuetracker.chatservice.exception.MessageNotFoundException;
import com.issuetracker.chatservice.exception.UnauthorizedChatException;
import com.issuetracker.chatservice.model.Conversation;
import com.issuetracker.chatservice.model.Message;
import com.issuetracker.chatservice.model.enums.MessageStatus;
import com.issuetracker.chatservice.repository.ConversationRepository;
import com.issuetracker.chatservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for managing chat messages
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
 
 private final MessageRepository messageRepository;
 private final ConversationRepository conversationRepository;
 private final ConversationService conversationService;
 
 /**
 * Send a message in a conversation
 */
 @Transactional
 public MessageResponse sendMessage(String senderId, SendMessageRequest request) {
 log.info("Sending message in conversation {} from user {}", request.getConversationId(), senderId);
 
 // Verify conversation exists and sender is participant
 Conversation conversation = conversationRepository.findById(request.getConversationId())
 .orElseThrow(() -> new ConversationNotFoundException("Conversation not found: " + request.getConversationId()));
 
 if (!conversation.getUserId().equals(senderId) && !conversation.getDepartmentId().equals(senderId)) {
 throw new UnauthorizedChatException("You are not a participant in this conversation");
 }
 
 // Determine sender role
 String senderRole = senderId.equals(conversation.getUserId()) ? "NORMAL_USER" : "DEPARTMENT";
 
 // Create message
 Message message = Message.builder()
 .conversationId(request.getConversationId())
 .senderId(senderId)
 .senderRole(senderRole)
 .content(request.getContent())
 .type(request.getType())
 .status(MessageStatus.SENT)
 .sentAt(LocalDateTime.now())
 .edited(false)
 .build();
 
 message = messageRepository.save(message);
 
 // Update conversation metadata
 conversationService.updateLastMessage(request.getConversationId(), request.getContent(), senderId);
 
 log.info("Message sent successfully: {}", message.getId());
 return mapToResponse(message);
 }
 
 /**
 * Get messages in a conversation
 */
 public Page<MessageResponse> getMessages(String conversationId, String requesterId, Pageable pageable) {
 // Verify requester is participant
 Conversation conversation = conversationRepository.findById(conversationId)
 .orElseThrow(() -> new ConversationNotFoundException("Conversation not found: " + conversationId));
 
 if (!conversation.getUserId().equals(requesterId) && !conversation.getDepartmentId().equals(requesterId)) {
 throw new UnauthorizedChatException("You are not a participant in this conversation");
 }
 
 Page<Message> messages = messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable);
 return messages.map(this::mapToResponse);
 }
 
 /**
 * Mark message as read
 */
 @Transactional
 public MessageResponse markAsRead(String messageId, String readBy) {
 Message message = messageRepository.findById(messageId)
 .orElseThrow(() -> new MessageNotFoundException("Message not found: " + messageId));
 
 // Cannot mark own message as read
 if (message.getSenderId().equals(readBy)) {
 return mapToResponse(message);
 }
 
 // Verify reader is participant
 Conversation conversation = conversationRepository.findById(message.getConversationId())
 .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));
 
 if (!conversation.getUserId().equals(readBy) && !conversation.getDepartmentId().equals(readBy)) {
 throw new UnauthorizedChatException("You are not a participant in this conversation");
 }
 
 if (message.getReadAt() == null) {
 message.setStatus(MessageStatus.READ);
 message.setReadAt(LocalDateTime.now());
 message = messageRepository.save(message);
 
 // Reset unread count
 conversationService.resetUnreadCount(message.getConversationId(), readBy);
 
 log.info("Message marked as read: {}", messageId);
 }
 
 return mapToResponse(message);
 }
 
 /**
 * Mark message as delivered
 */
 @Transactional
 public MessageResponse markAsDelivered(String messageId) {
 Message message = messageRepository.findById(messageId)
 .orElseThrow(() -> new MessageNotFoundException("Message not found: " + messageId));
 
 if (message.getDeliveredAt() == null) {
 message.setStatus(MessageStatus.DELIVERED);
 message.setDeliveredAt(LocalDateTime.now());
 message = messageRepository.save(message);
 }
 
 return mapToResponse(message);
 }
 
 /**
 * Edit a message
 */
 @Transactional
 public MessageResponse editMessage(String messageId, String newContent, String editorId) {
 Message message = messageRepository.findById(messageId)
 .orElseThrow(() -> new MessageNotFoundException("Message not found: " + messageId));
 
 // Only sender can edit
 if (!message.getSenderId().equals(editorId)) {
 throw new UnauthorizedChatException("You can only edit your own messages");
 }
 
 if (!message.isEdited()) {
 message.setOriginalContent(message.getContent());
 }
 
 message.setContent(newContent);
 message.setEdited(true);
 message.setEditedAt(LocalDateTime.now());
 
 message = messageRepository.save(message);
 log.info("Message edited: {}", messageId);
 
 return mapToResponse(message);
 }
 
 /**
 * Delete a message
 */
 @Transactional
 public void deleteMessage(String messageId, String deleterId) {
 Message message = messageRepository.findById(messageId)
 .orElseThrow(() -> new MessageNotFoundException("Message not found: " + messageId));
 
 // Only sender can delete
 if (!message.getSenderId().equals(deleterId)) {
 throw new UnauthorizedChatException("You can only delete your own messages");
 }
 
 messageRepository.delete(message);
 log.info("Message deleted: {}", messageId);
 }
 
 private MessageResponse mapToResponse(Message message) {
 return MessageResponse.builder()
 .id(message.getId())
 .conversationId(message.getConversationId())
 .senderId(message.getSenderId())
 .senderRole(message.getSenderRole())
 .senderName("User") // Would fetch from User Service in production
 .content(message.getContent())
 .type(message.getType())
 .status(message.getStatus())
 .sentAt(message.getSentAt())
 .deliveredAt(message.getDeliveredAt())
 .readAt(message.getReadAt())
 .edited(message.isEdited())
 .editedAt(message.getEditedAt())
 .build();
 }
}