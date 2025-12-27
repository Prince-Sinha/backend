
package com.issuetracker.userservice.service;

import com.issuetracker.userservice.dto.UpdateUserRequest;
import com.issuetracker.userservice.dto.UserDTO;
import com.issuetracker.userservice.exception.ResourceNotFoundException;
import com.issuetracker.userservice.model.User;
import com.issuetracker.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO updateUser(String userId, UpdateUserRequest request) {
            User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getCity() != null) {
                user.setCity(request.getCity());
            }
            if (request.getState() != null) {
                user.setState(request.getState());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress());
            }
            if (request.getPhoto() != null) {
                user.setPhoto(request.getPhoto());
            }

            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);

            return convertToDTO(user);
    }

    public void addSupportedPost(String userId, String postId) {
            User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            if (!user.getSupported().contains(postId)) {
                user.getSupported().add(postId);
                userRepository.save(user);
            }
    }

    public java.util.List<UserDTO> getAllDepartmentUsers() {
        java.util.List<User> departmentUsers = userRepository.findByRole(com.issuetracker.userservice.model.UserRole.DEPT);
        return departmentUsers.stream()
                                .map(this::convertToDTO)
                                .collect(java.util.stream.Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .photo(user.getPhoto())
                        .role(user.getRole())
                        .dept(user.getDept())
                        .city(user.getCity())
                        .state(user.getState())
                        .address(user.getAddress())
                        .supported(user.getSupported())
                        .createdAt(user.getCreatedAt())
                        .build();
    }
}