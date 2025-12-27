package com.issuetracker.chatservice.model.enums;

/**
 * Delivery and read status of a message
 */
public enum MessageStatus {
    SENT,        // Message sent by sender
    DELIVERED,   // Message delivered to recipient's device/server
    READ         // Message read by recipient
}