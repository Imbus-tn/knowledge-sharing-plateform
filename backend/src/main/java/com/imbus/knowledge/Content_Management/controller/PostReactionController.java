package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.ReactionRequest;
import com.imbus.knowledge.Content_Management.services.PostService;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content/posts")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostService postService;

    @PostMapping("/{postId}/react")
    public ResponseEntity<Void> reactToPost(
            @PathVariable Long postId,
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();

        postService.reactToPost(postId, request, userId);

        return ResponseEntity.noContent().build();
    }
}