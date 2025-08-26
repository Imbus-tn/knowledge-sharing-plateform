package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String title;        // ← Add
    private String description;  // ← Add
    private String category;     // ← Add
    private List<String> tags;   // ← Add
    private AuthorDto author;
    private LocalDateTime createdAt;
    private long commentCount;
    private long reactionCount;
    private boolean isFavorite;
    private int viewCount;
    private int likeCount;
    private int shareCount;

    // Static factory method
    public static PostResponse fromEntity(Post post, boolean isFavorite) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .title(post.getTitle())
                .description(post.getDescription())
                .category(post.getCategory())
                .tags(post.getTags())
                .author(AuthorDto.builder()
                        .name(post.getAuthor().getName())
                        .initials(post.getAuthor().getUsername().substring(0, 2))
                        .avatarUrl(post.getAuthor().getAvatarUrl())
                        .build())
                .createdAt(post.getCreatedAt())
                .commentCount(post.getComments().size())
                .reactionCount(post.getPostReactions().size())
                .viewCount(post.getViewCount())
                .isFavorite(isFavorite)
                .likeCount(post.getLikeCount())     // ✅ Add
                .shareCount(post.getShareCount())
                .build();
    }

    // Inner class with @Data and @Builder
    @Data
    @Builder
    public static class AuthorDto {
        private String name;
        private String initials;
        private String avatarUrl;

        public static AuthorDto from(User user) {
            return AuthorDto.builder()
                    .name(user.getName())
                    .initials(user.getUsername().substring(0, 2))
                    .avatarUrl(user.getAvatarUrl())
                    .build();
        }
    }
}