package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.FavoritePostResponse;
import com.imbus.knowledge.Content_Management.dto.PostResponse;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.repositories.FavoriteRepository;
import com.imbus.knowledge.Content_Management.services.FavoriteService;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing user's favorite posts.
 */
// FavoriteController.java
@RestController
@RequestMapping("/api/content/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRepository favoriteRepository; // ✅ Injected

    @GetMapping
    public ResponseEntity<List<PostResponse>> getFavoritesForCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        List<Post> favorites = favoriteRepository.findPostsByUserId(userId); // ✅ Correct
        List<PostResponse> responses = favorites.stream()
                .map(post -> PostResponse.fromEntity(post, true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}