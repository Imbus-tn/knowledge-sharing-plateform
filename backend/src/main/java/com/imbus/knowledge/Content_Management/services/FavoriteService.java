package com.imbus.knowledge.Content_Management.services;

import com.imbus.knowledge.Content_Management.dto.FavoritePostResponse;
import com.imbus.knowledge.Content_Management.entities.Post;
import com.imbus.knowledge.Content_Management.repositories.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.imbus.knowledge.Content_Management.dto.AuthorDto;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public List<FavoritePostResponse> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(fav -> mapToFavoritePostResponse(fav.getPost(), fav.getCreatedAt()))
                .toList();
    }

    private FavoritePostResponse mapToFavoritePostResponse(Post post, LocalDateTime favoritedAt) {
        return FavoritePostResponse.builder()
                .id(post.getId())
                .title(post.getTitle() != null ? post.getTitle() : "Untitled")
                .description(post.getDescription() != null ? post.getDescription() : "")
                .coverImage(post.getImageUrl())
                .author(AuthorDto.builder()
                        .name(post.getAuthor().getName())
                        .initials(post.getAuthor().getUsername().substring(0, 2))
                        .role(post.getAuthor().getRole())
                        .avatarUrl(post.getAuthor().getAvatarUrl())
                        .build())
                .createdAt(post.getCreatedAt())
                .likes(post.getPostReactions().size()) // Add
                .comments(post.getComments().size()) // Add
                .shares(post.getShares().size()) // Add
                .build();
    }
}