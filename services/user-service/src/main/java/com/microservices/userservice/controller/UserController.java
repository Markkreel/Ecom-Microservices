package com.microservices.userservice.controller;

import com.microservices.userservice.dto.UserProfileResponse;
import com.microservices.userservice.dto.UserProfileUpdateRequest;
import com.microservices.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        UserProfileResponse profile = userService.getProfile(token);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        UserProfileResponse updatedProfile = userService.updateProfile(token, request);
        return ResponseEntity.ok(updatedProfile);
    }
}