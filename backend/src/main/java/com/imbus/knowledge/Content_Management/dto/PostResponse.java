package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Post;
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

        // Null-safe access
        response.commentCount = post.getComments() != null ? post.getComments().size() : 0;
        response.reactionCount = post.getReactions() != null ? post.getReactions().size() : 0;

        // Set isFavorite based on current user
        return response;
    }

}
