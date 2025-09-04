// PostResponse.java
package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.dto.UserSummaryDto;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String title;
    private String description;
    private String category;
    private int viewCount;
    private int likeCount;
    private int shareCount;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserSummaryDto author;
    private boolean isFavorite;
    private List<ReactionResponse> reactions;
    private List<CommentResponse> comments;
    private Integer favorites;  // ← Count, not list
    private Integer shares;     // ← Count, not list
    private String linkUrl;
    private LinkPreviewDto linkPreview;

    /**
     * Factory method to convert Post entity to PostResponse DTO.
     * Only include data that was fetched (e.g., via @EntityGraph).
     */
    public static PostResponse fromEntity(Post post, boolean isFavorite) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .title(post.getTitle())
                .description(post.getDescription())
                .category(post.getCategory())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .shareCount(post.getShareCount())
                .tags(post.getTags() != null ? post.getTags() : List.of())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(UserSummaryDto.fromUser(post.getAuthor()))
                .isFavorite(isFavorite)

                // ✅ Safely include comments with author
                .comments(post.getComments() != null ?
                        post.getComments().stream()
                                .map(c -> CommentResponse.builder()
                                        .id(c.getId())
                                        .text(c.getText())
                                        .createdAt(c.getCreatedAt())
                                        .author(UserSummaryDto.fromUser(c.getAuthor()))
                                        .build())
                                .collect(Collectors.toList()) :
                        List.of())

                // ✅ Reactions: emoji only (count not included unless fetched)
                .reactions(post.getPostReactions() != null ?
                        post.getPostReactions().stream()
                                .map(r -> ReactionResponse.builder()
                                        .emoji(r.getEmoji())
                                        .build())
                                .collect(Collectors.toList()) :
                        List.of())

                // ✅ Favorites and Shares → return count only
                .favorites(post.getFavorites() != null ? post.getFavorites().size() : 0)
                .shares(post.getShares() != null ? post.getShares().size() : 0)

                // ✅ Link preview
                .linkUrl(post.getLinkUrl())
                .linkPreview(LinkPreviewDto.fromEntity(post.getLinkPreview()))

                .build();
    }
}