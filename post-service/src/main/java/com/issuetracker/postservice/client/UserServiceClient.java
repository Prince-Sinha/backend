
package com.issuetracker.postservice.client;

import com.issuetracker.postservice.dto.ApiResponse;
import com.issuetracker.postservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "user-service", url = "https://user-service-26b4.onrender.com")
public interface UserServiceClient {
    
    @GetMapping("/api/v1/users/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") String id);
    
    @PostMapping("/api/v1/auth/validate")
    ApiResponse<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader);
    
    @PostMapping("/api/v1/users/{userId}/support/{postId}")
    ApiResponse<String> addSupportedPost(@PathVariable("userId") String userId, 
                                         @PathVariable("postId") String postId);
}
