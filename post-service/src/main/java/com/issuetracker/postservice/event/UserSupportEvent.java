package com.issuetracker.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSupportEvent implements Serializable {
    private String userId;
    private String postId;
    private LocalDateTime timestamp;
}