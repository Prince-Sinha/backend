package com.issuetracker.opinionservice.client;

import com.issuetracker.opinionservice.config.FeignConfig;
import com.issuetracker.opinionservice.dto.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "user-service", 
    url = "${user-service.url}",
    configuration = FeignConfig.class
)
public interface UserServiceClient {
    
    @PostMapping("/api/v1/auth/validate")
    TokenValidationResponse validateToken(@RequestHeader("Authorization") String token);
}
