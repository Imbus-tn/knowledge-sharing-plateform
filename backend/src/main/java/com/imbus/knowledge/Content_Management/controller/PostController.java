package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.*;
import com.imbus.knowledge.Content_Management.services.PostService;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;

@RestController
@RequestMapping("/api/content/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // ==== POST CRUD ====

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        PostResponse createdPost = postService.createPost(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        PostResponse post = postService.getPostById(id, userId);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Page<PostResponse> posts = postService.getAllPosts(page, size, userId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        PostResponse updatedPost = postService.updatePost(id, request, userId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ==== INTERACTIONS ====

    // Toggle Favorite
    @PostMapping("/{postId}/favorite")
    public ResponseEntity<Void> toggleFavorite(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.toggleFavorite(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<Page<PostResponse>> getFavoritePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Page<PostResponse> favoritePosts = postService.getFavoritePostsByUserId(userId, page, size);
        return ResponseEntity.ok(favoritePosts);
    }

    // React to Post
    @PostMapping("/{postId}/react")
    public ResponseEntity<Void> reactToPost(
            @PathVariable Long postId,
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.reactToPost(postId, request, userId);
        return ResponseEntity.noContent().build();
    }

    // Comment on Post
    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        CommentResponse comment = postService.addCommentToPost(postId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // Reply to a Comment
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<CommentResponse> replyToComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        CommentResponse reply = postService.replyToComment(commentId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    // React to Comment
    @PostMapping("/comments/{commentId}/react")
    public ResponseEntity<Void> reactToComment(
            @PathVariable Long commentId,
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.reactToComment(commentId, request, userId);
        return ResponseEntity.noContent().build();
    }

    // Report Post
    @PostMapping("/{postId}/report")
    public ResponseEntity<Void> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User reporter = userDetails.getUser();
        postService.reportPost(postId, request, reporter);
        return ResponseEntity.noContent().build();
    }

    // Share Post
    @PostMapping("/{postId}/share")
    public ResponseEntity<Void> sharePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.sharePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==== HELPER METHODS ====

    private Long getUserIdFromUserDetails(UserDetailsImpl userDetails) {
        return userDetails.getUser().getId();
    }
}