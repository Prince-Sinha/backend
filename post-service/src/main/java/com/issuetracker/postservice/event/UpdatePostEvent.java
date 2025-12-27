package com.issuetracker.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostEvent {
    private String postId;
    private String title;
    private String description;
    private String location;
    private Boolean status;
    private LocalDateTime timestamp;
}
