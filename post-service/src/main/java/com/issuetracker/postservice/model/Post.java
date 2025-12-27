package com.issuetracker.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    
    @Id
    private String id;
    
    private String title;
    private String problemStatement;
    private String userId;
    private Department dept;
    
    @Builder.Default
    private List<String> postImg = new ArrayList<>();
    
    @Builder.Default
    private Integer ratingQuantity = 0;
    
    @Builder.Default
    private List<String> support = new ArrayList<>();
    
    @Builder.Default
    private Boolean status = false;  // false = unresolved, true = resolved
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}