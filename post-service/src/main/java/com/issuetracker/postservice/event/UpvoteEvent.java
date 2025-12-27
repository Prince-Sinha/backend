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
public class UpvoteEvent {
    private String postId;
    private String userId;
    private LocalDateTime timestamp;
}

