package com.microservices.userservice.service;

import com.microservices.userservice.config.JwtConfig;
import com.microservices.userservice.dto.*;
import com.microservices.userservice.model.User;
import com.microservices.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final EventPublisherService eventPublisherService;

    public String register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        userRepository.save(user);
        eventPublisherService.publishUserCreated(user.getId().toString(), user.getEmail());
        return jwtConfig.generateToken(user.getEmail());
    }

    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtConfig.generateToken(user.getEmail());
    }

    public String refreshToken(String token) {
        if (!jwtConfig.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        String email = jwtConfig.extractEmail(token);
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("User not found");
        }

        return jwtConfig.generateToken(email);
    }

    public UserProfileResponse getProfile(String token) {
        if (!jwtConfig.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        String email = jwtConfig.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public UserProfileResponse updateProfile(String token, UserProfileUpdateRequest request) {
        if (!jwtConfig.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        String email = jwtConfig.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        userRepository.save(user);

        eventPublisherService.publishUserUpdated(user.getId().toString(), user.getEmail(), List.of("name"));

        return UserProfileResponse.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}