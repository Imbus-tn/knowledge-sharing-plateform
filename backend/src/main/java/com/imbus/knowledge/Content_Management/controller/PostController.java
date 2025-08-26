package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.CreatePostRequest;
import com.imbus.knowledge.Content_Management.dto.PostResponse;
import com.imbus.knowledge.Content_Management.dto.ReactionRequest;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.repositories.FavoriteRepository;
import com.imbus.knowledge.Content_Management.services.ContentImageStorageService;
import com.imbus.knowledge.Content_Management.services.PostService;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/content/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ContentImageStorageService fileStorageService;

    // ✅ Create Post
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestPart("content") String content,
            @RequestPart(value = "imageUrl", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                imageUrl = fileStorageService.storeImage(imageFile);
            } catch (IOException e) {
                log.error("Failed to store image", e);
                return ResponseEntity.status(500).body(null);
            }
        }

        CreatePostRequest request = new CreatePostRequest();
        request.setContent(content);
        request.setImageUrl(imageUrl);

        Post createdPost = postService.createPost(request, userId);
        return ResponseEntity.ok(createdPost);
    }

    // ✅ Get All Posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Post> postPage = postService.getAllPosts(page, size);
        return ResponseEntity.ok(postPage.getContent());
    }

    // ✅ Get All Posts for Search
    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAllPostsForSearch() {
        Page<Post> postPage = postService.getAllPosts(0, 1000);
        List<PostResponse> responses = postPage.getContent().stream()
                .map(post -> PostResponse.fromEntity(post, false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ✅ Update Post
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        Post updatedPost = postService.updatePost(id, request, userId);
        return ResponseEntity.ok(updatedPost);
    }

    // ✅ Delete Post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Remove Favorite
    @DeleteMapping("/{postId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        postService.removeFavorite(postId, userId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get Post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Post post = postService.getPostById(id);
        postService.incrementViewCount(id);

        boolean isFavorite = false;
        if (userDetails != null) {
            User user = userRepository.findById(userDetails.getUser().getId()).orElse(null);
            if (user != null) {
                isFavorite = favoriteRepository.existsByUserAndPost(user, post);
            }
        }

        return ResponseEntity.ok(PostResponse.fromEntity(post, isFavorite));
    }

    // ✅ React to Post
    @PostMapping("/{postId}/react")
    public ResponseEntity<?> reactToPost(
            @PathVariable Long postId,
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Post post = postService.getPostById(postId);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            User user = userDetails.getUser();
            postService.reactToPost(postId, request, user.getId());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to react to post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to react");
        }
    }
    @PostMapping("/api/content/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileStorageService.storeImage(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed"));
        }
    }
    // ✅ Helper – Get user ID
    private Long getUserIdFromUserDetails(UserDetailsImpl userDetails) {
        return userDetails.getUser().getId();
    }
}