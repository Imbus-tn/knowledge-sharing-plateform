package com.imbus.knowledge.User_Management.controller;

import com.imbus.knowledge.User_Management.dto.InvitationDTO;
import com.imbus.knowledge.User_Management.dto.MailBody;
import com.imbus.knowledge.User_Management.entities.Invitation;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.entities.UserRole;
import com.imbus.knowledge.User_Management.repositories.InvitationRepository;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import com.imbus.knowledge.User_Management.services.EmailService;
import com.imbus.knowledge.User_Management.services.LocalAvatarStorageService;
import com.imbus.knowledge.User_Management.dto.UserResponse;
import com.imbus.knowledge.User_Management.dto.WarningRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> inviteUser(@RequestParam String email, @RequestParam UserRole role, @RequestParam(required = false) String message){
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String token = UUID.randomUUID().toString();
        Invitation invitation = Invitation.builder()
                .email(email)
                .role(role)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        invitationRepository.save(invitation);

        String registrationLink = "http://localhost:5173/register?token=" + token;
        MailBody mail = MailBody.builder()
                .to(email)
                .subject("Invitation to Join Imbus Knowledge Platform")
                .text(buildInvitationText(role, registrationLink, message))
                .build();
        emailService.sendSimpleMessage(mail);

        return ResponseEntity.ok("Invitation sent successfully");
    }

    public String buildInvitationText(UserRole role, String registrationLink, String message) {
        StringBuilder text = new StringBuilder();
        text.append("You've been invited to join as ").append(role).append(". Register here: ").append(registrationLink);
        if (message != null && !message.trim().isEmpty()) {
            text.append("\n\n").append("Message from admin: ").append(message);
        }

        return text.toString();
    }

    @GetMapping("/invitations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvitationDTO>> getRecentInvitations(){
        List<Invitation> invitations = invitationRepository.findAll(Sort.by(Sort.Direction.DESC, "expiresAt"));
        return ResponseEntity.ok(invitations.stream()
                .map(invitation -> new InvitationDTO(
                        invitation.getId(),
                        invitation.getEmail(),
                        invitation.getRole(),
                        invitation.isUsed(),
                        invitation.getExpiresAt(),
                        invitation.getStatus()))
                .collect(Collectors.toList()));
    }

    @PostMapping("/invitations/{id}/resend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resendInvitation(@PathVariable Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        invitation.setToken(UUID.randomUUID().toString());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitation.setUsed(false);
        invitationRepository.save(invitation);

        String registrationLink = "http://localhost:5173/register?token=" + invitation.getToken();
        MailBody mail = MailBody.builder()
                .to(invitation.getEmail())
                .subject("Invitation Reminder: Join Imbus Knowledge Platform")
                .text("Reminder: " + invitation.getRole() + " invitation. Register here: " + registrationLink)
                .build();
        emailService.sendSimpleMessage(mail);

        return ResponseEntity.ok("Invitation resent successfully");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> response = users.stream()
                .filter(user -> !UserRole.ADMIN.equals(user.getRole())) // Exclude ADMIN users
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @PutMapping("/users/{userId}/update-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateUserRoleWithMessage(
            @PathVariable Long userId,
            @RequestParam UserRole role,
            @RequestBody(required = false) Map<String, String> requestBody) {

        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update the user's role
        user.setRole(role);
        userRepository.save(user);

        // Optional: Send an email with the message if provided
        String message = requestBody != null ? requestBody.get("message") : null;
        if (message != null && !message.trim().isEmpty()) {
            MailBody mail = MailBody.builder()
                    .to(user.getEmail())
                    .subject("Your Role Has Been Updated")
                    .text("Your role has been updated to " + role.name() + ".\n\n" +
                            "Message from admin: " + message)
                    .build();
            try {
                emailService.sendSimpleMessage(mail);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send email: " + e.getMessage());
            }
        }

        // Prepare the success response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Role updated successfully for user: " + user.getEmail());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Find the user to delete
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User adminUser = userDetails.getUser();

        // Prevent admins from deleting themselves
        if (adminUser.getId() == userToDelete.getId()) {
            throw new RuntimeException("Cannot delete your own account");
        }

        // Log avatar deletion attempt
        System.out.println("Deleting avatar for user: " + userToDelete.getName());
        deleteAvatarIfExists(userToDelete.getAvatarUrl());

        // Delete the user's invitation if it exists
        invitationRepository.findByEmail(userToDelete.getEmail()).ifPresent(invitation -> {
            System.out.println("Deleting invitation for user: " + userToDelete.getEmail());
            invitationRepository.delete(invitation);
        });

        // Log related entities
        if (userToDelete.getRefreshToken() != null) {
            System.out.println("Clearing refresh token for user: " + userToDelete.getName());
            userToDelete.setRefreshToken(null);
        }
        if (userToDelete.getForgotPassword() != null) {
            System.out.println("Clearing forgot password record for user: " + userToDelete.getName());
            userToDelete.setForgotPassword(null);
        }

        // Save changes before deletion
        userRepository.save(userToDelete);

        // Log user deletion attempt
        System.out.println("Deleting user: " + userToDelete.getName());
        userRepository.deleteById(userId);

        // Return a success message
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Helper method to delete avatar
    private void deleteAvatarIfExists(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String fileName = avatarUrl.replaceFirst(".*/", "");
            Path filePath = Paths.get(LocalAvatarStorageService.UPLOAD_DIR, fileName);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but proceed with user deletion
                System.err.println("Failed to delete avatar: " + e.getMessage());
            }
        }
    }

    @PostMapping("/users/{userId}/warn")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> sendWarning(
            @PathVariable Long userId,
            @RequestBody WarningRequest request) {

        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build the email content
        MailBody mail = MailBody.builder()
                .to(user.getEmail())
                .subject("Account Warning")
                .text(request.message())
                .build();

        // Attempt to send the email
        try {
            emailService.sendSimpleMessage(mail);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send warning email: " + e.getMessage());
        }

        // Prepare the success response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Warning email sent successfully to " + user.getEmail());

        // Return the response with HTTP 200 OK status
        return ResponseEntity.ok(response);
    }
}
