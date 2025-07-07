package com.imbus.knowledge.User_Management.dto;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String bio;
    private String location;
    private String phoneNumber;
    private String github;
    private String linkedin;
    private String avatarUrl;
    private String initials;

    public static UserResponse fromEntity(User user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setBio(user.getBio());
        response.setLocation(user.getLocation());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setGithub(user.getGithub());
        response.setLinkedin(user.getLinkedin());
        response.setAvatarUrl(user.getAvatarUrl());
        // Generate initials from name
        if (user.getName() != null && !user.getName().isBlank()) {
            String[] parts = user.getName().split(" ");
            StringBuilder initials = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) {
                    initials.append(part.charAt(0));
                }
            }
            response.initials = initials.toString().toUpperCase();
        } else {
            response.initials = "??";
        }

        return response;
    }

    // Inner class for author info
    public static class AuthorDto {
        private String name;
        private String initials;
        private String avatarUrl;

        public static AuthorDto from(User user) {
            AuthorDto dto = new AuthorDto();
            dto.name = user.getName();
            dto.initials = user.getUsername().substring(0, 2); // First two letters
            dto.avatarUrl = user.getAvatarUrl();
            return dto;
        }
    }
}
