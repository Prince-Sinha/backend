


package com.issuetracker.userservice.dto;

import com.issuetracker.userservice.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String photo;
    private UserRole role;
    private String dept;
    private String city;
    private String state;
    private String address;
    private List<String> supported;
    private LocalDateTime createdAt;
}
