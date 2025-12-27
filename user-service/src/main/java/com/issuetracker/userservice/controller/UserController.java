package com.issuetracker.userservice.controller;

import com.issuetracker.userservice.dto.ApiResponse;
import com.issuetracker.userservice.dto.UpdateUserRequest;
import com.issuetracker.userservice.dto.UserDTO;
import com.issuetracker.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .status("success")
                .data(user)
                .build());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user information")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request) {
        UserDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .status("success")
                .message("User updated successfully")
                .data(user)
                .build());
    }

    @PostMapping("/{userId}/support/{postId}")
    @Operation(summary = "Add supported post to user")
    public ResponseEntity<ApiResponse<String>> addSupportedPost(
            @PathVariable String userId,
            @PathVariable String postId) {
        userService.addSupportedPost(userId, postId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status("success")
                .message("Post added to supported list")
                .build());
    }

    @GetMapping("/departments")
    @Operation(summary = "Get all department users for chat helpline")
    public ResponseEntity<ApiResponse<java.util.List<UserDTO>>> getAllDepartmentUsers() {
        java.util.List<UserDTO> departments = userService.getAllDepartmentUsers();
        return ResponseEntity.ok(ApiResponse.<java.util.List<UserDTO>>builder()
                .status("success")
                .data(departments)
                .build());
    }
}
