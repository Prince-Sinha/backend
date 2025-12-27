package com.issuetracker.opinionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String role;
    private String city;
    private String state;
    private String address;
}
