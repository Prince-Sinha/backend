package com.issuetracker.chatservice.model.enums;

/**
 * Type of message content
 */
public enum MessageType {
    TEXT,        // Plain text message
    IMAGE,       // Image attachment
    FILE,        // File attachment
    SYSTEM       // System-generated message (e.g., "User joined conversation")
}