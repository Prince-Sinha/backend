package com.issuetracker.opinionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "opinions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Opinion {
    
    @Id
    private String id;
    
    private String postId;
    private String userId;
    private String comment;
    
    // Nested Comments - Materialized Path Pattern (O(log n) operations)
    private String parentId;      // ID of parent comment (null for root comments)
    private String path;           // Materialized path e.g., "1/2/3" (child of child of root)
    private Integer depth;         // Nesting level: 0 for root, 1 for reply to root, etc.
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
