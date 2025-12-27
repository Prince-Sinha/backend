package com.issuetracker.postservice.dto;

import com.issuetracker.postservice.model.Department;
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
public class PostResponse {
    
    private String _id;  // MongoDB uses _id
    private String title;
    private String problemStatement;
    private String userId;
    private UserDTO user;  // Populated user object
    private Department dept;
    
    @Builder.Default
    private List<String> postImg = new ArrayList<>();
    
    @Builder.Default
    private Integer ratingQuantity = 0;
    
    @Builder.Default
    private List<String> support = new ArrayList<>();
    
    @Builder.Default
    private Boolean status = false;
    
    @Builder.Default
    private List<String> opinions = new ArrayList<>();  // For frontend compatibility
    
    @Builder.Default
    private Integer UpVote = 0;  // Capital U for frontend compatibility
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String photo;  // Single photo for frontend compatibility
}