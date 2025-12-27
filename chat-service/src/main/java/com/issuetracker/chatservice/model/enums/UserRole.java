package com.issuetracker.chatservice.model.enums;

/**
 * Role of user participating in chat
 */
public enum UserRole {
    PUBLIC,    // Regular user who can only chat with departments
    DEPT      // Department user who can chat with normal users
}