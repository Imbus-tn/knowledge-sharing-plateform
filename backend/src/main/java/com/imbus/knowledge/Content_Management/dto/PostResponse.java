package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private AuthorDto author;
    private LocalDateTime createdAt;
    private long commentCount;
    private long reactionCount;
    private boolean isFavorite;

    // Static factory method
    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();
        response.id = post.getId();
        response.content = post.getContent();
        response.imageUrl = post.getImageUrl();
        response.author = AuthorDto.from(post.getAuthor());
        response.createdAt = post.getCreatedAt();
        response.commentCount = post.getComments().size();
        response.reactionCount = post.getReactions().size();
        // Set isFavorite based on current user
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
            dto.initials = user.getUsername().substring(0, 2); // Simplified
            dto.avatarUrl = user.getAvatarUrl();
            return dto;
        }
    }
}
