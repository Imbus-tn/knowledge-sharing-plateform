// src/main/java/com/imbus/knowledge/Content_Management/controller/CommentController.java

package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.CommentRequest;
import com.imbus.knowledge.Content_Management.entities.Comment;
import com.imbus.knowledge.Content_Management.services.PostService;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class CommentController {

    private final PostService postService;

    // ✅ Add comment to post
    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long userId = userDetails.getUser().getId();
            Comment comment = postService.addCommentToPost(postId, request, userId);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            log.error("Failed to add comment", e);
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ Reply to comment
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<Comment> replyToComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long userId = userDetails.getUser().getId();
            Comment reply = postService.replyToComment(commentId, request, userId);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            log.error("Failed to reply to comment", e);
            return ResponseEntity.status(500).build();
        }
    }
}