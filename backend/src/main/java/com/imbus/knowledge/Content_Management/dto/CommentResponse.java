package com.imbus.knowledge.Content_Management.dto;

import com.imbus.knowledge.Content_Management.entities.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentResponse {
    private Long id;
    private String text;
    private AuthorDto author;
    private LocalDateTime createdAt;
    private Integer replyCount;
    private List<CommentResponse> replies = new ArrayList<>();

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.id = comment.getId();
        response.text = comment.getText();
        response.author = AuthorDto.from(comment.getAuthor());
        response.createdAt = comment.getCreatedAt();

        if (comment.getReplies() != null) {
            response.replyCount = comment.getReplies().size();
            response.replies = comment.getReplies().stream()
                    .map(CommentResponse::from)
                    .toList();
        } else {
            response.replyCount = 0;
        }

        return response;
    }
}