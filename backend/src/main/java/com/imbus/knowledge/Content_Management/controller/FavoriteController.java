package com.imbus.knowledge.Content_Management.controller;

import com.imbus.knowledge.Content_Management.dto.FavoritePostResponse;
import com.imbus.knowledge.Content_Management.services.FavoriteService;
import com.imbus.knowledge.User_Management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user's favorite posts.
 */
@RestController
@RequestMapping("/api/content/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Get all favorite posts for the currently authenticated user.
     * @param userDetails Authenticated user details
     * @return List of favorite posts
     */
    @GetMapping
    public ResponseEntity<List<FavoritePostResponse>> getFavoritesForCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        List<FavoritePostResponse> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(favorites);
    }


}