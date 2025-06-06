package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.*;
import com.imbus.knowledge.Content_Management.entities.Post;
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
    public ResponseEntity<Post> createPost(
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Post createdPost = postService.createPost(request, userId);

        // Ensure all collections are initialized to empty (not null)
        if (createdPost.getReactions() == null) {
            createdPost.setReactions(new ArrayList<>());
        }
        if (createdPost.getComments() == null) {
            createdPost.setComments(new ArrayList<>());
        }
        if (createdPost.getFavorites() == null) {
            createdPost.setFavorites(new HashSet<>());
        }
        if (createdPost.getShares() == null) {
            createdPost.setShares(new ArrayList<>());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Post updatedPost = postService.updatePost(id, request, userId);
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
    public ResponseEntity<Void> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.addCommentToPost(postId, request, userId);
        return ResponseEntity.noContent().build();
    }

    // Reply to a Comment
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<Void> replyToComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.replyToComment(commentId, request, userId);
        return ResponseEntity.noContent().build();
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
        return userDetails.getUser().getId(); // Adjust based on actual implementation
    }
}