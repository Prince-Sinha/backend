package com.issuetracker.postservice.event;

import com.issuetracker.postservice.model.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String problemStatement;
    private String userId;
    private Department dept;
    private List<String> postImg;
    private LocalDateTime timestamp;
}
