
package com.issuetracker.chatservice.exception;

/**
 * Exception thrown when user attempts unauthorized chat operation
 * e.g., Normal user trying to chat with another normal user
 */
public class UnauthorizedChatException extends RuntimeException {
    
    public UnauthorizedChatException(String message) {
        super(message);
    }
    
    public UnauthorizedChatException(String message, Throwable cause) {
        super(message, cause);
    }
}