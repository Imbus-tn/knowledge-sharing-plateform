package com.imbus.knowledge.User_Management.controller;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import com.imbus.knowledge.User_Management.services.AuthService;
import com.imbus.knowledge.User_Management.dto.ProfileUpdateRequest;
import com.imbus.knowledge.User_Management.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/{userId}/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getProfile(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @PutMapping("/{userId}/profile")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        User updatedUser = authService.updateUserProfile(userId, request);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    @PostMapping("/{userId}/avatar")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<UserResponse> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        // Validate image type
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        User updatedUser = authService.uploadAvatar(userId, file);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

 }
