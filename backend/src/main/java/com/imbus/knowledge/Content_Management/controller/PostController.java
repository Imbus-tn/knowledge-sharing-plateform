package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.CreatePostRequest;
import com.imbus.knowledge.Content_Management.dto.PostResponse;
import com.imbus.knowledge.Content_Management.dto.PostSummaryDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    public ResponseEntity<PostResponse> createPost(
            @RequestPart("content") String content,
            @RequestPart(value = "imageUrl", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long userId = getUserIdFromUserDetails(userDetails);

            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = fileStorageService.storeImage(imageFile);
            }

            CreatePostRequest request = new CreatePostRequest();
            request.setContent(content);
            request.setImageUrl(imageUrl);

            Post createdPost = postService.createPost(request, userId);
            PostResponse response = PostResponse.fromEntity(createdPost, false);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to create post", e);
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ Get All Posts
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getAllPosts(pageable);

        List<PostResponse> responses = postPage.getContent().stream()
                .map(post -> PostResponse.fromEntity(post, false))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ✅ Get All Posts for Search
    @GetMapping("/all")
    public ResponseEntity<List<PostSummaryDto>> getAllPostsForSearch() {
        try {
            Pageable pageable = PageRequest.of(0, 1000);
            Page<Post> postPage = postService.getAllPosts(pageable);

            List<PostSummaryDto> summaries = postPage.getContent().stream()
                    .map(PostSummaryDto::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            log.error("Failed to load posts for search", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<Void> sharePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            postService.sharePost(postId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to share post", e);
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ Update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            Post updatedPost = postService.updatePost(id, request, userId);
            PostResponse response = PostResponse.fromEntity(updatedPost, false);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update post", e);
            return ResponseEntity.status(500).build();
        }
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

    // ✅ Add Favorite
    @PostMapping("/{postId}/favorite")
    public ResponseEntity<Void> addFavorite(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            log.info("User {} toggling favorite for post {}", userId, postId);

            postService.toggleFavorite(postId, userId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Failed to toggle favorite for post {} by user {}", postId, userDetails.getUser().getId(), e);
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ Remove Favorite
    @DeleteMapping("/{postId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable("postId") Long postId,
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

        try {
            log.info("Fetching post with ID: {}", id);

            Post post = postService.getPostById(id);
            if (post == null) {
                log.warn("Post not found for ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            postService.incrementViewCount(id);

            User user = null;
            if (userDetails != null) {
                user = userRepository.findById(userDetails.getUser().getId()).orElse(null);
            }

            boolean isFavorite = user != null &&
                    favoriteRepository.existsByUserAndPost(user, post);

            PostResponse response = PostResponse.fromEntity(post, isFavorite);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Server error fetching post ID: {}", id, e);
            return ResponseEntity.status(500).body(null);
        }
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

    // ✅ Upload Image to Post
    @PostMapping("/{postId}/image")
    public ResponseEntity<PostResponse> uploadPostImage(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            String imageUrl = fileStorageService.storeImage(file);
            Long userId = getUserIdFromUserDetails(userDetails);

            Post updatedPost = postService.updatePostImage(postId, imageUrl, userId);

            boolean isFavorite = userDetails != null &&
                    favoriteRepository.existsByUserAndPost(userDetails.getUser(), updatedPost);

            PostResponse response = PostResponse.fromEntity(updatedPost, isFavorite);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to upload image for post " + postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ Helper – Get user ID
    private Long getUserIdFromUserDetails(UserDetailsImpl userDetails) {
        return userDetails.getUser().getId();
    }
}