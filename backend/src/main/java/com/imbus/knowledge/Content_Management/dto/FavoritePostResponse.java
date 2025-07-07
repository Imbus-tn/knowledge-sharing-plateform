package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritePostResponse {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private AuthorDto author;
    private LocalDateTime createdAt;
}