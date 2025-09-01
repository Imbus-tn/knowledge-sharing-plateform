// src/main/java/com/imbus/knowledge/User_Management/dto/UserSummaryDto.java
package com.imbus.knowledge.User_Management.dto;

import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private String location;
    private String phoneNumber;
    private String github;
    private String linkedin;
    private String avatarUrl;
    private UserRole role;
    private String initials;

    // ✅ Add this factory method
    public static UserSummaryDto fromUser(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .bio(user.getBio())
                .location(user.getLocation())
                .phoneNumber(user.getPhoneNumber())
                .github(user.getGithub())
                .linkedin(user.getLinkedin())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .initials(user.getInitials())
                .build();
    }
}