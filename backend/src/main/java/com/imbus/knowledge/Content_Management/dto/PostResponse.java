package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.entities.Reaction;
import com.imbus.knowledge.Content_Management.entities.ReactionType;
import com.imbus.knowledge.User_Management.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private VideoDto videoUrl;
    private AuthorDto author;
    private String createdAt;
    private int comments;
    private int shares;
    private boolean isFavorite;
    private List<ReactionDto> reactions = new ArrayList<>();
    private List<TagDto> tags = new ArrayList<>();

    // Static factory method
    public static PostResponse from(Post post, boolean isFavorite, User currentUser) {
        PostResponse response = new PostResponse();
        response.id = String.valueOf(post.getId()); // Convert Long to String
        response.title = post.getTitle();
        response.content = post.getContent();
        response.imageUrl = post.getImageUrl();

        if (post.getVideoUrl() != null && post.getVideoThumbnail() != null) {
            response.videoUrl = VideoDto.builder()
                    .url(post.getVideoUrl())
                    .thumbnail(post.getVideoThumbnail())
                    .build();
        } else {
            response.videoUrl = null;
        }

        response.author = AuthorDto.from(post.getAuthor());
        response.createdAt = post.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        response.comments = Optional.ofNullable(post.getComments()).map(List::size).orElse(0);
        response.shares = Optional.ofNullable(post.getShares()).map(List::size).orElse(0);
        response.isFavorite = isFavorite;
        if (post.getTags() != null) {
            response.tags = post.getTags().stream()
                    .map(tag -> TagDto.builder()
                            .name(tag.getName())
                            .color(tag.getColor())
                            .build())
                    .collect(Collectors.toList());
        } else {
            response.tags = new ArrayList<>();
        }

        // Map reactions to ReactionDto
        Map<String, Set<String>> emojiToUsers = new HashMap<>();
        for (Reaction r : post.getReactions()) {
            String emoji = ReactionType.getEmoji(r.getType());
            String userId = String.valueOf(r.getUser().getId());
            emojiToUsers.computeIfAbsent(emoji, k -> new HashSet<>()).add(userId);
        }

        List<ReactionDto> reactionDtos = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : emojiToUsers.entrySet()) {
            reactionDtos.add(
                    ReactionDto.builder()
                            .emoji(entry.getKey())
                            .count(entry.getValue().size())
                            .users(new ArrayList<>(entry.getValue()))
                            .build()
            );
        }

        response.reactions = reactionDtos;
        response.isFavorite = isFavorite;
        // Set isFavorite based on current user
        return response;
    }
    // New method for mapping from PostSummary DTO
    public static PostResponse fromSummary(PostSummary summary, boolean isFavorite) {
        PostResponse response = new PostResponse();
        response.id = String.valueOf(summary.getId());
        response.title = summary.getTitle();
        response.content = summary.getContent();
        response.imageUrl = summary.getImageUrl();

        if (summary.getVideoUrl() != null && summary.getVideoThumbnail() != null) {
            response.videoUrl = VideoDto.builder()
                    .url(summary.getVideoUrl())
                    .thumbnail(summary.getVideoThumbnail())
                    .build();
        } else {
            response.videoUrl = null;
        }

        response.author = AuthorDto.from(summary.getAuthor());
        response.createdAt = summary.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        response.comments = summary.getCommentCount();
        response.shares = summary.getShareCount();
        response.isFavorite = isFavorite;
        response.tags = summary.getTagDtos();
        response.reactions = summary.getReactionDtos();

        return response;
    }

}
