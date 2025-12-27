package com.issuetracker.opinionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpinionResponse {
    
    private String id;
    private String postId;
    private String userId;
    private String opinion;  // Changed from 'comment' to match frontend expectation
    
    // Nested user data populated from user-service
    private UserDTO user;
    
    // Nested Comments - Materialized Path Pattern
    private String parentId;      
    private String path;           
    private Integer depth;
    private Integer replyCount;    // Number of direct children
    
    // For hierarchical response - nested children
    @Builder.Default
    private List<OpinionResponse> children = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Helper method to add child
    public void addChild(OpinionResponse child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }
}