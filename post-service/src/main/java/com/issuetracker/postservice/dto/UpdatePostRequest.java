package com.issuetracker.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {
    
    private String title;
    private String description;
    private String location;
    private Boolean status;  // true = resolved, false = unresolved
}
