package com.issuetracker.chatservice.model.enums;

/**
 * Status of a conversation between user and department
 */
public enum ConversationStatus {
    ACTIVE,      // Conversation is currently active
    CLOSED,      // Conversation has been closed by either party
    ARCHIVED     // Conversation has been archived for record keeping
}