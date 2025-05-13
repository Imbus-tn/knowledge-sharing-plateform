package com.imbus.knowledge.User_Management.dto;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime lastLogin;
    private String status;
    private boolean enabled;
    private String bio;
    private String location;
    private String phoneNumber;
    private String github;
    private String linkedin;
    private String avatarUrl;

    public static UserResponse fromEntity(User user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setLastLogin(user.getLastLogin());
        response.setStatus(computeStatus(user.getLastLogin()));
        response.setEnabled(user.isEnabled());
        response.setBio(user.getBio());
        response.setLocation(user.getLocation());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setGithub(user.getGithub());
        response.setLinkedin(user.getLinkedin());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }
    private static String computeStatus(LocalDateTime lastLogin) {
        if (lastLogin == null) return "Offline";// No login history

        // Calculate minutes between lastLogin and current time
        long minutesSinceLogin = ChronoUnit.MINUTES.between(lastLogin, LocalDateTime.now());

        // Return "Online" if logged in within last 5 minutes
        return minutesSinceLogin <= 5 ? "Online" : "Offline";
    }
}
