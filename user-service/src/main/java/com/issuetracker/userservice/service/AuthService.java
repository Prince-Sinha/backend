
package com.issuetracker.userservice.service;

import com.issuetracker.userservice.dto.*;
import com.issuetracker.userservice.exception.BadRequestException;
import com.issuetracker.userservice.exception.ResourceNotFoundException;
import com.issuetracker.userservice.model.User;
import com.issuetracker.userservice.repository.UserRepository;
import com.issuetracker.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse signup(SignupRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered");
        }

        User user = User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .city(request.getCity())
                        .state(request.getState())
                        .address(request.getAddress())
                        .role(request.getRole())
                        .photo(request.getPhoto())
                        .createdAt(LocalDateTime.now())
                        .build();

        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        UserDTO userDTO = convertToDTO(user);

        return AuthResponse.builder()
                .status("success")
                .token(token)
                .user(userDTO)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                        .orElseThrow(() -> new BadRequestException("Incorrect phoneNumber or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect phoneNumber or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        UserDTO userDTO = convertToDTO(user);

        return AuthResponse.builder()
                .status("success")
                .token(token)
                .user(userDTO)
                .build();
    }

    public AuthResponse loginAdmin(LoginAdminRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new BadRequestException("Incorrect email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        UserDTO userDTO = convertToDTO(user);

        return AuthResponse.builder()
                .status("success")
                .token(token)
                .user(userDTO)
                .build();
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .photo(user.getPhoto())
                .role(user.getRole())
                .city(user.getCity())
                .state(user.getState())
                .address(user.getAddress())
                .supported(user.getSupported())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

