package com.imbus.knowledge.User_Management.services;

import com.imbus.knowledge.User_Management.entities.UserRole;
import com.imbus.knowledge.User_Management.exception.ValidationException;
import com.imbus.knowledge.User_Management.dto.AuthResponse;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.dto.LoginRequest;
import com.imbus.knowledge.User_Management.dto.ProfileUpdateRequest;
import com.imbus.knowledge.User_Management.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final LocalAvatarStorageService localAvatarStorageService;

    public void register(String email, UserRole role, RegisterRequest registerRequest){
        Map<String, List<String>> errors = new HashMap<>();

        // Full Name Validation
        List<String> nameErrors = new ArrayList<>();
        String trimmedName = registerRequest.name().trim();

        // Length check (3-50 characters)
        if (trimmedName.length() < 5 || trimmedName.length() > 50) {
            nameErrors.add("Must be 5-50 characters");
        }

        // Character validation (letters, spaces, hyphens, apostrophes)
        if (!trimmedName.matches("^[a-zA-Z '-]+$")) {
            nameErrors.add("Only letters, spaces, hyphens, and apostrophes allowed");
        }

        // Split into parts (first + last name)
        String[] nameParts = trimmedName.split("\\s+");
        List<String> nonEmptyParts = Arrays.stream(nameParts)
                .filter(part -> !part.isEmpty())
                .toList();

        // Check for at least two parts
        if (nonEmptyParts.size() < 2) {
            nameErrors.add("Full name must include first and last name");
        }

        // Check each part length (min 2 characters)
        for (String part : nonEmptyParts) {
            if (part.length() < 2) {
                nameErrors.add("Each name part must be at least 2 characters");
            }
        }

        // Check if name is already taken
        if (userRepository.existsByName(trimmedName)) {
            nameErrors.add("Name already taken");
        }

        if (!nameErrors.isEmpty()) {
            errors.put("name", nameErrors);
        }


        // Password validation
        List<String> passwordErrors = new ArrayList<>();
        String password = registerRequest.password();
        String repeatPassword = registerRequest.repeatPassword();
        if (password.length() < 8 ){
            passwordErrors.add("Minimum 8 characters");
        }
        if (password.length() > 64){
            passwordErrors.add("Maximum 64 characters");
        }
        if (!password.matches("^(?=.*[A-Z]).+$")){
            passwordErrors.add("Uppercase letter required");
        }
        if (!password.matches("^(?=.*[a-z]).+$")){
            passwordErrors.add("Lowercase letter required");
        }
        if (!password.matches("^(?=.*\\d).+$")){
            passwordErrors.add("Number required");
        }
        if (!password.matches("^(?=.*[^A-Za-z0-9]).+$")){
            passwordErrors.add("Special character required (!@#$%^&*...)");
        }
        if (!password.equals(repeatPassword)){
            passwordErrors.add("Passwords do not match");
        }
        if (!passwordErrors.isEmpty()){
            errors.put("password", passwordErrors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        var user = User.builder()
                .name(trimmedName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public AuthResponse login(LoginRequest loginRequest){
        Map<String, List<String>> errors = new HashMap<>();

        // Email validation
        String email = loginRequest.getEmail().trim();
        if (email.isEmpty()){
            errors.put("email", List.of("Email is required"));
        } else if (!isValidEmail(email)) {
            errors.put("email", List.of("Invalid email format"));
        }

        // Password validation
        String password = loginRequest.getPassword();
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", List.of("Password is required"));
        } else if (password.length() < 8) {
            errors.put("password", List.of("Minimum 8 characters required"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

    var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        // Update lastLogin
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

    var accessToken = jwtService.generateToken(user);
    var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

    return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getRefreshToken())
            .build();
    }

    @Transactional
    public User updateUserProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate Full Name
        if (request.name() != null && !request.name().equals(user.getName())) {
            String trimmedName = request.name().trim();
            Map<String, List<String>> errors = new HashMap<>();

            // Check if name exists
            if (userRepository.existsByName(trimmedName)) {
                errors.put("name", List.of("Name already taken"));
            }

            // Validate new name
            if (trimmedName.length() < 3 || trimmedName.length() > 50) {
                errors.computeIfAbsent("name", k -> new ArrayList<>())
                        .add("Must be 3-50 characters");
            }
            if (!trimmedName.matches("^[a-zA-Z '-]+$")) {
                errors.computeIfAbsent("name", k -> new ArrayList<>())
                        .add("Only letters, spaces, hyphens, and apostrophes allowed");
            }
            String[] parts = trimmedName.split("\\s+");
            List<String> nonEmpty = Arrays.stream(parts)
                    .filter(p -> !p.isEmpty())
                    .toList();
            if (nonEmpty.size() < 2) {
                errors.computeIfAbsent("name", k -> new ArrayList<>())
                        .add("Full name required (first & last)");
            }
            for (String part : nonEmpty) {
                if (part.length() < 2) {
                    errors.computeIfAbsent("name", k -> new ArrayList<>())
                            .add("Each part ≥2 characters");
                }
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            user.setName(trimmedName);
        }
        
        // Update only non-null fields
        if (request.bio() != null) user.setBio(request.bio());
        if (request.location() != null) user.setLocation(request.location());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());
        if (request.github() != null) user.setGithub(request.github());
        if (request.linkedin() != null) user.setLinkedin(request.linkedin());

        // Only update avatarUrl if it is explicitly provided in the request
        if (request.avatarUrl() != null) {
            String newAvatarUrl = request.avatarUrl();
            String oldAvatarUrl = user.getAvatarUrl();

            // Only delete if the new URL is different and the old one is a local file
            if (!newAvatarUrl.equals(oldAvatarUrl) && oldAvatarUrl != null && oldAvatarUrl.startsWith("/uploads/")) {
                String fileName = oldAvatarUrl.replaceFirst(".*/", "");
                Path oldFilePath = Paths.get(LocalAvatarStorageService.UPLOAD_DIR, fileName);
                try {
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete old avatar", e);
                }
            }
            user.setAvatarUrl(newAvatarUrl);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User uploadAvatar(Long userId, MultipartFile file) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Delete previous avatar if exists
            String oldAvatarUrl = user.getAvatarUrl();
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                String fileName = oldAvatarUrl.replaceFirst(".*/", ""); // Extract filename from URL
                Path oldFilePath = Paths.get(LocalAvatarStorageService.UPLOAD_DIR, fileName);
                try {
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete old avatar", e);
                }
            }

            // Save new avatar
            String avatarUrl = localAvatarStorageService.storeAvatar(file);
            user.setAvatarUrl(avatarUrl);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Avatar upload failed: " + e.getMessage(), e);
        }
    }

    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }
}
