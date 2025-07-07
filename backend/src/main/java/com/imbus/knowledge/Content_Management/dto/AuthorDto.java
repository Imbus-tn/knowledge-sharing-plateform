package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.*;
import lombok.Builder;
import java.time.LocalDateTime;// Inner DTO for author info
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
 public class AuthorDto {
    private String name;
    private String initials;
    private UserRole role;
    private String avatarUrl;
}
