package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.CreatePostRequest;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.services.PostService;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import com.imbus.knowledge.User_Management.entities.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // Create Post – Only Contributor or Admin
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        Post createdPost = postService.createPost(request, userId);

        return ResponseEntity.status(201).body(createdPost);
    }

    // Get All Posts – Public or Authenticated
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(postService.getAllPosts(page, size).getContent());
    }

    // Get Post by ID
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // Update Post – Only Author or Admin
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        Post updatedPost = postService.updatePost(id, request, userId);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete Post – Only Admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{postId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.removeFavorite(postId, userId);
        return ResponseEntity.noContent().build();
    }

    // Helper – Get user ID
    private Long getUserIdFromUserDetails(UserDetailsImpl userDetails) {
        return userDetails.getUser().getId();
    }
}