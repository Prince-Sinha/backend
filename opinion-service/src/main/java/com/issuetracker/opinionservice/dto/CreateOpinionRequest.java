
package com.issuetracker.opinionservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOpinionRequest {
    
    @NotBlank(message = "Post ID is required")
    private String postId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Comment is required")
    private String comment;
    
    // For nested comments - null for root comments, parent opinion ID for replies
    private String parentId;
}