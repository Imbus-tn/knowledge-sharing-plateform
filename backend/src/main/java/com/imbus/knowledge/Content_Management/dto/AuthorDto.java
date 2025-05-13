package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.User_Management.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private Long id;
    private String name;
    private String initials;
    private String avatarUrl;

    public static AuthorDto from(User user) {
        AuthorDto dto = new AuthorDto();
        dto.id = user.getId();
        dto.name = user.getName();

        String username = user.getUsername();
        dto.initials = username != null && username.length() >= 2 ? username.substring(0, 2) : "??";

        dto.avatarUrl = user.getAvatarUrl();
        return dto;
    }
}