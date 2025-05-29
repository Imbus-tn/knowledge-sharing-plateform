package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Comment;
import com.imbus.knowledge.Content_Management.entities.Reaction;
import com.imbus.knowledge.Content_Management.entities.ReactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommentResponse {
    private String id;
    private String text;
    private AuthorDto author;
    private String createdAt;
    private int replyCount;
    private List<CommentResponse> replies = new ArrayList<>();
    private List<ReactionDto> reactions = new ArrayList<>();

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.id = String.valueOf(comment.getId());
        response.text = comment.getText();
        response.author = AuthorDto.from(comment.getAuthor());
        response.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        response.replyCount = comment.getReplies().size();
        response.replies = comment.getReplies().stream()
                .map(CommentResponse::from)
                .toList();

        Map<String, Set<String>> emojiToUsers = new HashMap<>();
        for (Reaction r : comment.getReactions()) {
            String emoji = ReactionType.getEmoji(r.getType());
            String userId = String.valueOf(r.getUser().getId());
            emojiToUsers.computeIfAbsent(emoji, k -> new HashSet<>()).add(userId);
        }

        List<ReactionDto> reactionDtos = emojiToUsers.entrySet().stream()
                .map(entry -> ReactionDto.builder()
                        .emoji(entry.getKey())
                        .count(entry.getValue().size())
                        .users(new ArrayList<>(entry.getValue()))
                        .build())
                .toList();

        response.reactions = reactionDtos;

        return response;
    }
}