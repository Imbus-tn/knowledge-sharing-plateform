package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.User_Management.dto.UserSummaryDto;
import com.imbus.knowledge.Content_Management.services.PostService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private List<FavoriteResponse> favorites;
    private List<ShareResponse> shares;
    private String linkUrl;
    private LinkPreviewDto linkPreview;
    // Static factory method
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
                .tags(post.getTags())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(UserSummaryDto.fromUser(post.getAuthor()))
                .isFavorite(isFavorite)

                // ✅ Link preview
                .linkUrl(post.getLinkUrl())
                .linkPreview(LinkPreviewDto.fromEntity(post.getLinkPreview()))

                // ✅ Comments
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

                // ✅ Reactions
                .reactions(post.getPostReactions() != null ?
                        post.getPostReactions().stream()
                                .map(r -> ReactionResponse.builder()
                                        .emoji(r.getEmoji())
                                        .build())
                                .collect(Collectors.toList()) :
                        List.of())

                // ✅ Favorites
                .favorites(post.getFavorites() != null ?
                        post.getFavorites().stream()
                                .map(f -> FavoriteResponse.builder()
                                        .id(f.getId())
                                        .createdAt(f.getCreatedAt())
                                        .build())
                                .collect(Collectors.toList()) :
                        List.of())

                // ✅ Shares
                .shares(post.getShares() != null ?
                        post.getShares().stream()
                                .map(s -> ShareResponse.builder()
                                        .id(s.getId())
                                        .sharedAt(s.getSharedAt())
                                        .build())
                                .collect(Collectors.toList()) :
                        List.of())

                .build();
    }}