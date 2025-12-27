

package com.issuetracker.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
 
 @Id
 private String id;
 
 private String name;
 
 @Indexed(unique = true)
 private String email;
 
 @Indexed(unique = true)
 private String phoneNumber;
 
 private String password;
 
 private String photo;
 
 @Builder.Default
 private UserRole role = UserRole.PUBLIC;
 
 private String dept; // Department name for DEPT role users
 
 private String city;
 
 private String state;
 
 private String address;
 
 @Builder.Default
 private List<String> supported = new ArrayList<>(); // Post IDs that user supported
 
 private LocalDateTime passwordChangedAt;
 
 private String passwordResetToken;
 
 private LocalDateTime passwordResetExpire;
 
 @Builder.Default
 private LocalDateTime createdAt = LocalDateTime.now();
 
 @Builder.Default
 private LocalDateTime updatedAt = LocalDateTime.now();
 
 @Builder.Default
 private boolean active = true;
}