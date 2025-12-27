package com.issuetracker.chatservice.exception;

/**
 * Exception thrown when a conversation is not found
 */
public class ConversationNotFoundException extends RuntimeException {
    
    public ConversationNotFoundException(String message) {
        super(message);
    }
    
    public ConversationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
