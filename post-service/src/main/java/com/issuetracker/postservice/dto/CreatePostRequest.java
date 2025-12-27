package com.issuetracker.postservice.dto;

import com.issuetracker.postservice.model.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Problem statement is required")
    private String problemStatement;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Department is required")
    private Department dept;
    
    private List<String> postImg;
}