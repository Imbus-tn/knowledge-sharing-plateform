package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Post;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostSummaryDto {
    private Integer id;
    private String title;
    private String description;
    private String content;
    private String category;
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    private List<String> tags;
    private AuthorDto author;

    // ✅ Convert Post → PostSummaryDto
    public static PostSummaryDto fromEntity(Post post) {
        if (post == null) return null;

        return PostSummaryDto.builder()
                .id(Math.toIntExact(post.getId()))
                .title(post.getTitle())
                .description(post.getDescription())
                .content(post.getContent())
                .category(post.getCategory())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .shareCount(post.getShareCount())
                .tags(post.getTags() != null ? post.getTags() : List.of())
                .author(AuthorDto.fromEntity(post.getAuthor()))
                .build();
    }
}